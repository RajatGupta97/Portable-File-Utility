import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.*;

public class ZipUtils
{
	private ProgressMonitor pmonitor;
	private long totalBytes=0;
	private int zipBytes = 0;
	private List<File> fileList;
	private static String OUTPUT_ZIP_FILE = "Folder.zip";
	private static String SOURCE_FOLDER = "";
	public ZipUtils(File[] selectedFiles)
	{
		fileList = new ArrayList<File>();
		for(int i = 0; i < selectedFiles.length; i++)
		{
			generateFileList(selectedFiles[i]);
		}
		OUTPUT_ZIP_FILE = selectedFiles[0].getParent()+"\\Folder.zip";
		SOURCE_FOLDER = selectedFiles[0].getParent();
		
		if(SOURCE_FOLDER.endsWith("\\"))
		{
			SOURCE_FOLDER = SOURCE_FOLDER.substring(0, SOURCE_FOLDER.length()-1);
		}
		
		System.out.println("Output File : " + OUTPUT_ZIP_FILE + "\n Source Folder : " + SOURCE_FOLDER);
		
		for(File f : fileList)
		{
			if(f.isFile())
				totalBytes += f.length();
		}
		
		pmonitor = new ProgressMonitor(Main.frame, "Zipping File", "Zipping.......", 0, (int)totalBytes);
		zipIt(OUTPUT_ZIP_FILE);	   
	}
	
	public void zipIt(String zipFile)
	{
	   byte[] buffer = new byte[1024*10];
	   String source = "";
	   FileOutputStream fos = null;
	   ZipOutputStream zos = null;
	   try
	   {

	     fos = new FileOutputStream(zipFile);
	     zos = new ZipOutputStream(fos);
		 zos.setLevel(9);
	     System.out.println("Output to Zip : " + zipFile);
	     FileInputStream in = null;
	
	     for (File file : this.fileList)
	     {
	        System.out.println("File Added : " + file.getName());
	        ZipEntry ze = new ZipEntry(generateZipEntry(file.getAbsolutePath()));
	        zos.putNextEntry(ze);
	        try
	        {
	           in = new FileInputStream(file.getAbsolutePath());
	           pmonitor.setNote("Zipping: " + file.getName());
	           int len;
	           while ((len = in.read(buffer)) > 0)
	           {
	           	  zipBytes += len;
	           	  pmonitor.setProgress(zipBytes);
	              zos.write(buffer, 0, len);
	              
	           }
	           in.close();
	        }
	        catch(Exception e)
	        {
	           e.printStackTrace();
	        }
	     }
	
	     zos.closeEntry();
	     System.out.println("Folder successfully compressed");
	     pmonitor.close();
	
		JOptionPane.showMessageDialog(Main.frame, "File compressed successfully");
	  }
	  catch (IOException ex)
	  {
	     ex.printStackTrace();
	  }
	  finally
	  {
	     try
	     {
	        zos.close();
	     }
	     catch (IOException e)
	     {
	        e.printStackTrace();
	     }
	  }
	}

	public void generateFileList(File node)
	{
	
	  // add file only
	  if (node.isFile())
	  {
	  	 System.out.println("Adding : " + node.getName());
	     fileList.add(node);
	  }
	
	  if (node.isDirectory())
	  {
	  	 System.out.println("Found Folder while Adding: " + node.getName());
	     File[] subNote = node.listFiles();
	     for (File filename : subNote)
	     {
	        generateFileList(filename);
	     }
	  }
	}
	
	private String generateZipEntry(String file){
		
		System.out.println("Creating entry : " + file.substring(SOURCE_FOLDER.length()+1, file.length()));
    	return file.substring(SOURCE_FOLDER.length()+1, file.length());
    }
}    