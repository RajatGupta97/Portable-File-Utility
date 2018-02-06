import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.*;
 
public class UnZipUtils {
    private static final int BUFFER_SIZE = 1024*10;
    
    private ProgressMonitor pmonitor;
	private long totalBytes=0;
	private int zipBytes = 0;
	
	
    public void unzip(String zipFilePath, String destDirectory) {
        try{
	        
	        File f = new File(zipFilePath);
	        totalBytes = f.length();
	        
	        pmonitor = new ProgressMonitor(Main.frame, "Un-Zipping File", "Un-Zipping.......", 0, (int)totalBytes);
	        
	        File destDir = new File(destDirectory);
	        if (!destDir.exists()) {
	            destDir.mkdir();
	        }
	        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
	        ZipEntry entry = zipIn.getNextEntry();
	        while (entry != null) {
	            String filePath = destDirectory + File.separator + entry.getName();
	            System.out.println("Un-Zipping: " + entry.getName());
	            pmonitor.setNote("Un-Zipping: " + entry.getName());
	            if (!entry.isDirectory()) {
	                extractFile(zipIn, filePath);
	            } else {
	                File dir = new File(filePath);
	                dir.mkdir();
	            }
	            zipIn.closeEntry();
	            entry = zipIn.getNextEntry();
	        }
	        zipIn.close();
	        pmonitor.close();
	        JOptionPane.showMessageDialog(Main.frame, "File compressed successfully");
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    	File file = new File(filePath).getParentFile();
    	if(!file.exists())
    		file.mkdirs();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
            zipBytes += read;
            pmonitor.setProgress(zipBytes);
        }
        bos.close();
    }
}