package utils.crypto.adv;

import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyPVectorX;
import com.aol.cyclops2.types.foldable.Evaluation;
import cyclops.collections.immutable.VectorX;
import cyclops.companion.Reducers;
import cyclops.stream.ReactiveSeq;
import org.pcollections.PVector;
import utils.crypto.adv.bulletproof.GeneratorParams;
import utils.crypto.adv.bulletproof.algebra.Group;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.commitments.PeddersenCommitment;
import utils.crypto.adv.bulletproof.linearalgebra.GeneratorVector;
import utils.crypto.adv.bulletproof.multirangeproof.MultiRangeProofProver;
import utils.crypto.adv.bulletproof.multirangeproof.MultiRangeProofVerifier;
import utils.crypto.adv.bulletproof.rangeproof.RangeProof;
import utils.crypto.adv.bulletproof.rangeproof.RangeProofProver;
import utils.crypto.adv.bulletproof.rangeproof.RangeProofVerifier;
import utils.crypto.adv.bulletproof.util.ProofUtils;

import java.math.BigInteger;

public class BulletProofUtils {

    public static BigInteger randomNumber() {
        return ProofUtils.randomNumber();
    }

    public static GeneratorParams generateParameters(int size, Group curve) {
        return GeneratorParams.generateParams(size, curve);
    }

    public static GroupElement generateCommitment(GeneratorParams parameters, BigInteger number, BigInteger randomness) {
        return parameters.getBase().commit(number, randomness);
    }

    public static GeneratorVector generateCommitments(Group curve, VectorX<PeddersenCommitment> witness) {
        GroupElement[] elements = new GroupElement[witness.size()];
        for (int i = 0; i < witness.size(); i++) {
            elements[i] = witness.get(i).getCommitment();
        }
        return GeneratorVector.from(new LazyPVectorX((PVector) null, ReactiveSeq.of(elements), Reducers.toPVector(), Evaluation.LAZY), curve);
    }

    public static PeddersenCommitment generateWitness(GeneratorParams parameters, BigInteger number, BigInteger randomness) {
        return new PeddersenCommitment<>(parameters.getBase(), number, randomness);
    }

    public static VectorX<PeddersenCommitment> generateWitness(GeneratorParams parameters, BigInteger... values) {
        return VectorX.of(values).map(x -> new PeddersenCommitment<>(parameters.getBase(), x)).materialize();
    }

    public static RangeProof generateProof(GeneratorParams parameters, GroupElement commitment, PeddersenCommitment witness) {
        return new RangeProofProver().generateProof(parameters, commitment, witness);
    }

    public static RangeProof generateProof(GeneratorParams parameters, GeneratorVector commitments, VectorX witness) {
        return new MultiRangeProofProver<>().generateProof(parameters, commitments, witness);
    }

    public static boolean verify(GeneratorParams parameters, GroupElement commitment, RangeProof proof) {
        try {
            new RangeProofVerifier().verify(parameters, commitment, proof);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean verify(GeneratorParams parameters, GeneratorVector commitments, RangeProof proof) {
        try {
            MultiRangeProofVerifier verifier = new MultiRangeProofVerifier<>();
            verifier.verify(parameters, commitments, proof);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
