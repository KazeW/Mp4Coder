package me.zuichu.mp4coder.example.google;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import me.zuichu.mp4coder.IsoFile;
import me.zuichu.mp4coder.ParsableBox;
import me.zuichu.mp4coder.tools.Path;

/**
 *
 */
public class BoxReplacer {
    public static void replace(Map<String, ParsableBox> replacements, File file) throws IOException {
        IsoFile isoFile = new IsoFile(new RandomAccessFile(file, "r").getChannel());
        Map<String, ParsableBox> replacementSanitised = new HashMap<String, ParsableBox>();
        Map<String, Long> positions = new HashMap<String, Long>();
        // TODO not working atm
        for (Map.Entry<String, ParsableBox> e : replacements.entrySet()) {
            ParsableBox b = Path.getPath(isoFile, e.getKey());
            //replacementSanitised.put(Path.createPath( b ), e.getValue());
            //positions.put(Path.createPath( b ), b.getOffset());
            assert b.getSize() == e.getValue().getSize();
        }
        isoFile.close();
        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
        for (String path : replacementSanitised.keySet()) {
            ParsableBox b = replacementSanitised.get(path);
            long pos = positions.get(path);
            fileChannel.position(pos);
            b.getBox(fileChannel);
        }
        fileChannel.close();
    }

}
