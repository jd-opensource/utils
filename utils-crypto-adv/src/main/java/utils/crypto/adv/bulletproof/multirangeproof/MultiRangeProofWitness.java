package utils.crypto.adv.bulletproof.multirangeproof;

import cyclops.collections.immutable.VectorX;
import utils.crypto.adv.bulletproof.commitments.PeddersenCommitment;

/**
 * Created by buenz on 7/1/17.
 */
public class MultiRangeProofWitness {
    private final VectorX<PeddersenCommitment> commitments;


    public MultiRangeProofWitness(VectorX<PeddersenCommitment> commitments) {

        this.commitments = commitments;
    }

    public VectorX<PeddersenCommitment> getCommitments() {
        return commitments;
    }
}
