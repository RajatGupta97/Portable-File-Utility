import java.io.*;
import javax.swing.*;

public class FileSplitMerge
{
	private ProgressMonitor pmonitor;
	public static int BUFFER = 1024*10;
	
	public void splitFile(File source, File destFolder, int splitSize)
	{
		
		String fileName = source.getName();
		String fileNameWithoutExt = fileName.substring(0, fileName.indexOf("."));
		String splitFolder = fileNameWithoutExt+"_Split";
		
		int numFiles = (int) (source.length() / splitSize);
		if(((int)source.length() % splitSize) != 0)
		{
			numFiles++;
		}
		
		pmonitor = new ProgressMonitor(Main.frame, "Splitting Files", "Splitting.......", 0, numFiles);
		try{
			FileInputStream fis = new FileInputStream(source);
			BufferedInputStream bis = new BufferedInputStream(fis, BUFFER);
			
			File destination = new File(destFolder.getAbsolutePath() + "\\"+splitFolder);
			System.out.println(destination.getAbsolutePath());
			destination.mkdirs();
					
			for(int i = 0 ; i < numFiles ; i++){
				int count = 0;
				System.out.println("Splitting : " + destination+(i+1) + fileName);
				pmonitor.setNote("Creating Part : " + (i+1) + fileName);
				FileOutputStream fos = new FileOutputStream(new File(destination, (i+1) + fileName));
				BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
				int ch;
				while((ch = bis.read()) != -1)
				{
					bos.write(ch);
					count++;
					if(count == splitSize)
					{
						break;
					}
				}
				pmonitor.setProgress(i+1);
				bos.close();
				fos.close();
			}
			bis.close();
			fis.close();
			JOptionPane.showMessageDialog(Main.frame, "File Splitted successfully");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void mergeFiles(File firstSplit, File destination)
	{
		try{
			String fileName = firstSplit.getName().substring(1);
			File parent = firstSplit.getParentFile();
			
			FileOutputStream fos = new FileOutputStream(destination);
			BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
			
			pmonitor = new ProgressMonitor(Main.frame, "Merging Files", "Merging.......", 0, Integer.parseInt(Main.txtSplitNum.getText()));
			
			int i = 1;
			while(true){
				File splittedFile = new File(parent, i + ""+ fileName);
				System.out.println("Merging : " + splittedFile.getName());
				
				
				if(!splittedFile.exists())
				{
					break;
				}
				
				pmonitor.setNote("Merging : " + splittedFile.getName());
				FileInputStream fis = new FileInputStream(splittedFile);
				BufferedInputStream bis = new BufferedInputStream(fis, BUFFER);
				int ch;
				while((ch = bis.read()) != -1)
				{
					bos.write(ch);
				}
				bis.close();
				fis.close();
				i++;
				pmonitor.setProgress(i);
			}
			bos.close();
			fos.close();
			JOptionPane.showMessageDialog(Main.frame, "File Merged successfully");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}