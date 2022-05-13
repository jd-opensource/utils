package utils.crypto.adv;

import cyclops.collections.immutable.VectorX;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.crypto.adv.bulletproof.GeneratorParams;
import utils.crypto.adv.bulletproof.algebra.Group;
import utils.crypto.adv.bulletproof.algebra.GroupElement;
import utils.crypto.adv.bulletproof.algebra.Secp256k1;
import utils.crypto.adv.bulletproof.commitments.PeddersenCommitment;
import utils.crypto.adv.bulletproof.linearalgebra.GeneratorVector;
import utils.crypto.adv.bulletproof.rangeproof.RangeProof;
import utils.serialize.json.JSONSerializeUtils;

import java.math.BigInteger;

@RunWith(Parameterized.class)
public class BulletProofUtilsTest {

    @Parameterized.Parameters(name = "curve")
    public static Object[] data() {
        return new Object[]{new Secp256k1()/*, new BN128Group(), new C0C0Group()*/};
    }

    @Parameterized.Parameter
    public Group<?> curve;

    @Test
    public void testSingleRangeProof() {
        BigInteger number = BigInteger.valueOf(10);
        BigInteger randomness = BulletProofUtils.randomNumber();
        GeneratorParams parameters = BulletProofUtils.generateParameters(8, curve);
        GroupElement commitment = BulletProofUtils.generateCommitment(parameters, number, randomness);
        PeddersenCommitment witness = BulletProofUtils.generateWitness(parameters, number, randomness);
        RangeProof proof = BulletProofUtils.generateProof(parameters, commitment, witness);

        // commitment serialize/deserialize
        String commitmentJson = JSONSerializeUtils.serializeToJSON(commitment);
        System.out.println(commitmentJson);
        commitment = JSONSerializeUtils.deserializeFromJSON(commitmentJson, GroupElement.class);

        // proof serialize/deserialize
        String proofJson = JSONSerializeUtils.serializeToJSON(proof);
        System.out.println(proofJson);
        proof = JSONSerializeUtils.deserializeFromJSON(proofJson, RangeProof.class);

        Assert.assertTrue(BulletProofUtils.verify(parameters, commitment, proof));
    }

    @Test
    public void testMultiRangeProof() {
        BigInteger[] values = new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(15)};
        GeneratorParams parameters = BulletProofUtils.generateParameters(8, curve);
        VectorX witness = BulletProofUtils.generateWitness(parameters, values);
        GeneratorVector commitments = BulletProofUtils.generateCommitments(curve, witness);
        RangeProof proof = BulletProofUtils.generateProof(parameters, commitments, witness);

        // commitments serialize/deserialize
        String commitmentsJson = JSONSerializeUtils.serializeToJSON(commitments);
        System.out.println(commitmentsJson);
        commitments = JSONSerializeUtils.deserializeFromJSON(commitmentsJson, GeneratorVector.class);

        // proof serialize/deserialize
        String proofJson = JSONSerializeUtils.serializeToJSON(proof);
        System.out.println(proofJson);
        proof = JSONSerializeUtils.deserializeFromJSON(proofJson, RangeProof.class);

        Assert.assertTrue(BulletProofUtils.verify(parameters, commitments, proof));
    }
}
