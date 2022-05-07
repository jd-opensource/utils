package utils.crypto.adv.bulletproof.rangeproof;

import cyclops.collections.immutable.VectorX;
import utils.crypto.adv.bulletproof.GeneratorParams;
import utils.crypto.adv.bulletproof.VerificationFailedException;
import utils.crypto.adv.bulletproof.Verifier;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.innerproduct.EfficientInnerProductVerifier;
import utils.crypto.adv.bulletproof.linearalgebra.FieldVector;
import utils.crypto.adv.bulletproof.linearalgebra.GeneratorVector;
import utils.crypto.adv.bulletproof.linearalgebra.PeddersenBase;
import utils.crypto.adv.bulletproof.linearalgebra.VectorBase;
import utils.crypto.adv.bulletproof.util.ProofUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by buenz on 7/1/17.
 */
public class RangeProofVerifier<T extends GroupElement<T>> implements Verifier<GeneratorParams<T>, T, RangeProof<T>> {


    @Override
    public void verify(GeneratorParams<T> params, T input, RangeProof<T> proof, Optional<BigInteger> salt) throws VerificationFailedException {
        VectorBase<T> vectorBase = params.getVectorBase();
        PeddersenBase<T> base = params.getBase();
        int n = vectorBase.getGs().size();
        T a = proof.getaI();
        T s = proof.getS();

        BigInteger q = params.getGroup().groupOrder();
        BigInteger y;

        if (salt.isPresent()) {
            y = ProofUtils.computeChallenge(q, salt.get(), input, a, s);
        } else {
            y = ProofUtils.computeChallenge(q, input, a, s);

        }
        FieldVector ys = FieldVector.from(VectorX.iterate(n, BigInteger.ONE, y::multiply), q);

        BigInteger z = ProofUtils.challengeFromints(q, y);

        BigInteger zSquared = z.pow(2).mod(q);
        BigInteger zCubed = z.pow(3).mod(q);

        FieldVector twos = FieldVector.from(VectorX.iterate(n, BigInteger.ONE, bi -> bi.shiftLeft(1)), q);
        FieldVector twoTimesZSquared = twos.times(zSquared);
        GeneratorVector<T> tCommits = proof.gettCommits();

        BigInteger x = ProofUtils.computeChallenge(q, z, tCommits);

        BigInteger tauX = proof.getTauX();
        BigInteger mu = proof.getMu();
        BigInteger t = proof.getT();
        BigInteger k = ys.sum().multiply(z.subtract(zSquared)).subtract(zCubed.shiftLeft(n).subtract(zCubed));
        T lhs = base.commit(t.subtract(k), tauX);
        T rhs = tCommits.commit(Arrays.asList(x, x.pow(2))).add(input.multiply(zSquared));
        equal(lhs, rhs, "Polynomial identity check failed, LHS: %s, RHS %s");
        BigInteger uChallenge = ProofUtils.challengeFromints(q, x, tauX, mu, t);
        T u = base.g.multiply(uChallenge);
        GeneratorVector<T> hs = vectorBase.getHs();
        GeneratorVector<T> gs = vectorBase.getGs();
        GeneratorVector<T> hPrimes = hs.haddamard(ys.invert());
        FieldVector hExp = ys.times(z).add(twoTimesZSquared);
        T P = a.add(s.multiply(x)).add(gs.sum().multiply(z.negate())).add(hPrimes.commit(hExp)).subtract(base.h.multiply(mu)).add(u.multiply(t));
        VectorBase<T> primeBase = new VectorBase<>(gs, hPrimes, u);

        EfficientInnerProductVerifier<T> verifier = new EfficientInnerProductVerifier<>();
        verifier.verify(primeBase, P, proof.getProductProof(), uChallenge);

    }
}
