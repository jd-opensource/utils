package utils.crypto.adv.bulletproof.rangeproof;

import utils.crypto.adv.bulletproof.GeneratorParams;
import utils.crypto.adv.bulletproof.ProofSystem;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.commitments.PeddersenCommitment;

/**
 * Created by buenz on 7/1/17.
 */
public class RangeProofSystem<T extends GroupElement<T>> implements ProofSystem<GeneratorParams<T>, T, PeddersenCommitment<T>, RangeProof<T>, RangeProofProver<T>, RangeProofVerifier<T>> {
    @Override
    public RangeProofProver<T> getProver() {

        return new RangeProofProver<>();
    }

    @Override
    public RangeProofVerifier<T> getVerifier() {
        return new RangeProofVerifier<>();
    }


}
