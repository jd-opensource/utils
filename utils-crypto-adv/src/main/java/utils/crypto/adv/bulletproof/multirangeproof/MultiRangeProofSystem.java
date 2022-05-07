package utils.crypto.adv.bulletproof.multirangeproof;

import cyclops.collections.immutable.VectorX;
import utils.crypto.adv.bulletproof.GeneratorParams;
import utils.crypto.adv.bulletproof.ProofSystem;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.commitments.PeddersenCommitment;
import utils.crypto.adv.bulletproof.linearalgebra.GeneratorVector;
import utils.crypto.adv.bulletproof.rangeproof.RangeProof;

/**
 * Created by buenz on 7/1/17.
 */
public class MultiRangeProofSystem<T extends GroupElement<T>> implements ProofSystem<GeneratorParams<T>, GeneratorVector<T>, VectorX<PeddersenCommitment<T>>, RangeProof<T>, MultiRangeProofProver<T>, MultiRangeProofVerifier<T>> {
    @Override
    public MultiRangeProofProver<T> getProver() {
        return new MultiRangeProofProver<>();
    }

    @Override
    public MultiRangeProofVerifier<T> getVerifier() {
        return new MultiRangeProofVerifier<>();
    }


}
