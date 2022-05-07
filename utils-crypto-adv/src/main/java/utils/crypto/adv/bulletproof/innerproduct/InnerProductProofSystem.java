package utils.crypto.adv.bulletproof.innerproduct;

import cyclops.collections.immutable.VectorX;
import utils.crypto.adv.bulletproof.ProofSystem;
import utils.crypto.adv.bulletproof.algebra.Group;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.linearalgebra.GeneratorVector;
import utils.crypto.adv.bulletproof.linearalgebra.VectorBase;
import utils.crypto.adv.bulletproof.util.ProofUtils;

/**
 * Created by buenz on 6/28/17.
 */
public class InnerProductProofSystem<T extends GroupElement<T>> implements ProofSystem<VectorBase<T>, T, InnerProductWitness, InnerProductProof<T>, InnerProductProver<T>, EfficientInnerProductVerifier<T>> {


    @Override
    public InnerProductProver<T> getProver() {
        return new InnerProductProver<>();
    }

    @Override
    public EfficientInnerProductVerifier<T> getVerifier() {
        return new EfficientInnerProductVerifier<>();
    }

    public <T extends GroupElement<T>> VectorBase<T> generatePublicParams(int size, Group<T> group) {
        VectorX<T> gs = VectorX.range(0, size).map(i -> ProofUtils.paddedHash("G", i)).map(group::mapInto);
        VectorX<T> hs = VectorX.range(0, size).map(i -> ProofUtils.paddedHash("H", i)).map(group::mapInto);

        GeneratorVector<T> gVector = new GeneratorVector<>(gs, group);
        GeneratorVector<T> hVector = new GeneratorVector<>(hs, group);
        T v = group.mapInto(ProofUtils.hash("V"));
        // GeneratorVector gs = GeneratorVector.from(VectorX.generate(size, ProofUtils::randomNumber).map(ECConstants.G::multiply));
        // GeneratorVector hs = GeneratorVector.from(VectorX.generate(size, ProofUtils::randomNumber).map(ECConstants.G::multiply));
        // ECPoint v=ECConstants.G.multiply(ProofUtils.randomNumber());
        return new VectorBase<>(gVector, hVector, v);
    }
}