package me.zuichu.mp4coder.example.coder.commonencryption;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import me.zuichu.mp4coder.Container;
import me.zuichu.mp4coder.muxer.InMemRandomAccessSourceImpl;
import me.zuichu.mp4coder.muxer.Movie;
import me.zuichu.mp4coder.muxer.Track;
import me.zuichu.mp4coder.muxer.builder.DefaultMp4Builder;
import me.zuichu.mp4coder.muxer.container.mp4.MovieCreator;
import me.zuichu.mp4coder.muxer.tracks.CencDecryptingTrackImpl;
import me.zuichu.mp4coder.muxer.tracks.CencEncryptedTrack;
import me.zuichu.mp4coder.muxer.tracks.CencEncryptingTrackImpl;
import me.zuichu.mp4coder.tools.ByteBufferByteChannel;

/**
 * This examples
 * <ol>
 * <li>reads an MP4 file into a movie object</li>
 * <li>creates an encrypted representation from plain representation</li>
 * <li>writes the encrypted representation into a byte array</li>
 * <li>reads the encrypted representation into a movie object</li>
 * <li>creates a plain representation from an encrypted representation</li>
 * <li>writes the decrypted representation to a file</li>
 * </ol>
 */
public class CencEncryptDecrypt {
    public static void main(String[] args) throws IOException {
        DefaultMp4Builder mp4Builder = new DefaultMp4Builder();

        // (1) READING
        Movie mOrig = MovieCreator.build(CencEncryptDecrypt.class.getProtectionDomain().getCodeSource().getLocation().getFile() + "/1365070268951.mp4");


        // (2) ENCRYPT
        Movie mEncryptOut = new Movie();
        SecretKey sk = new SecretKeySpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, "AES");
        for (Track track : mOrig.getTracks()) {
            mEncryptOut.addTrack(new CencEncryptingTrackImpl(track, UUID.randomUUID(), sk, true));
        }

        // (3) WRITE ENCRYPTED
        Container cEncrypted = mp4Builder.build(mEncryptOut);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cEncrypted.writeContainer(Channels.newChannel(baos));

        FileOutputStream fos = new FileOutputStream("output-enc.mp4");
        fos.write(baos.toByteArray());

        /// (4) READ ENCRYPTED
        Movie mEncryptIn = MovieCreator.build(new ByteBufferByteChannel(baos.toByteArray()), new InMemRandomAccessSourceImpl(baos.toByteArray()), "inmem");
        Movie mDecrypt = new Movie();

        // (5) DECRYPT
        for (Track track : mEncryptIn.getTracks()) {
            if (track instanceof CencEncryptedTrack) {
                mDecrypt.addTrack(new CencDecryptingTrackImpl((CencEncryptedTrack) track, sk));
            } else {
                mDecrypt.addTrack(track);
            }
        }

        // (6) WRITE PLAIN
        Container cDecrypted = mp4Builder.build(mDecrypt);
        FileOutputStream fos2 = new FileOutputStream("output.mp4");
        cDecrypted.writeContainer(fos2.getChannel());

    }
}
