import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

public class Main implements ActionListener
{
	public static JFrame frame;
	private JTabbedPane tabbedPane;
	private JPanel zipPanel;
	private JPanel splitPanel;
	private JPanel mergePanel;
	private JButton btnSelectZip, btnSelectUnzip, btnDoZip, btnDoUnZip;
	private JTextField txtZipSelection, txtUnZipSelection;
	
	private JButton btnSelectFile, btnSelectMFolder, btnDoSplit, btnDoMerge, btnSelectSplitFolder, btnSelectMLocation;
	private JTextField txtFileSelection, txtMFileSelection, txtFileSize, txtSplitSize, txtSplitFileLocation,  txtMergeName, txtMergeLocation;
	public static JTextField  txtSplitNum;
	private File[] selectedFiles;
	private File selectedFile;
	
	private File selectedSplitFile, selectedSplitFolder;
	private File selectedMergeFile, selectedMergedFolder;
	
	public Main()
	{
		frame = new JFrame("File Tool");
		frame.setSize(500,380);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		tabbedPane  = new JTabbedPane();
		
		zipPanel = new JPanel();
		zipPanel.setLayout(null);
		
		splitPanel = new JPanel();
		splitPanel.setLayout(null);
		
		mergePanel = new JPanel();
		mergePanel.setLayout(null);
		
		tabbedPane.addTab("Zip-Unzip Files",null, zipPanel, "Zip-Unzip Files");
		tabbedPane.addTab("Split Files", null, splitPanel, "Split Files");
		tabbedPane.addTab("Merge Files", null, mergePanel, "Merge Files");
		
		frame.add(tabbedPane);
		
		renderZipPanel();
		renderFilePanel();
		renderFileMergePanel();
			
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnSelectZip)
		{
			System.out.println("Selecting file/folder to zip");
			selectedFiles = selectFiles(false);
			
			if(selectedFiles == null)
				return;
				
			String txt_path = "";
			for(int i = 0; i < selectedFiles.length; i++)
			{
				if(txt_path.length() > 0) 
					txt_path += "; ";
					
				txt_path += selectedFiles[i].getName();
			}
			txtZipSelection.setText(txt_path);
			
		}
		else if(e.getSource() == btnSelectUnzip)
		{
			System.out.println("Selecting  zip file");
			selectedFile = selectFile(false);
			
			if(selectedFile == null)
				return;
				
			
			txtUnZipSelection.setText(selectedFile.getName());
			
			
		}
		else if(e.getSource() == btnDoZip)
		{
			if(selectedFiles != null)
			{
				
				Thread t = new Thread(){
					public void run()
					{
						new ZipUtils(selectedFiles);
					}
				};
				t.start();
			}
			else{
				JOptionPane.showMessageDialog(frame, "Please select files to zip");
			}
		}
		else if(e.getSource() == btnDoUnZip)
		{
			if(selectedFile != null)
			{
				Thread t = new Thread(){
					public void run()
					{
						new UnZipUtils().unzip(selectedFile.getAbsolutePath(), selectedFile.getParentFile().getAbsolutePath());
												
					}
				};
				t.start();
			}
			else{
				JOptionPane.showMessageDialog(frame, "Please selecte zip file to unzip.");
			}
		}
		else if(e.getSource() == btnSelectFile)
		{
			System.out.println("Selecting  file to Split");
			selectedSplitFile = selectFile(false);
			
			if(selectedSplitFile == null)
				return;
			
			if(selectedSplitFile.isDirectory()){
				JOptionPane.showMessageDialog(frame, "Please select a file to split.");
			}	
			
			txtFileSelection.setText(selectedSplitFile.getAbsolutePath());
			txtFileSize.setText(selectedSplitFile.length()+"");
		}
		else if(e.getSource() == btnSelectSplitFolder)
		{
			System.out.println("Selecting  folder to Split");
			selectedSplitFolder = selectFile(true);
			
			if(selectedSplitFolder == null)
				return;
			
			if(selectedSplitFolder.isFile()){
				JOptionPane.showMessageDialog(frame, "Please select a folder to split.");
			}	
			
			txtSplitFileLocation.setText(selectedSplitFolder.getAbsolutePath());
		}
		else if(e.getSource() == btnDoSplit)
		{
			System.out.println("Splitting files.....");
			if(selectedSplitFile == null)
			{
				JOptionPane.showMessageDialog(frame, "Please select a file to split.");
				return;
			}
			
			if(selectedSplitFolder == null)
			{
				JOptionPane.showMessageDialog(frame, "Please select a folder to split.");
				return;
			}
			
			String splitSize = txtSplitSize.getText();
			try{
				int i = Integer.parseInt(splitSize);
				
				if(i <= 0)
				{
					throw new NumberFormatException();
				}
			}
			catch(Exception ee){
				JOptionPane.showMessageDialog(frame, "Please enter a valid split size.");
				return;
			}
			
			
			Thread t = new Thread(){
				public void run()
				{
					FileSplitMerge fsm = new FileSplitMerge();
					fsm.splitFile(selectedSplitFile, selectedSplitFolder, Integer.parseInt(splitSize));
				}
			};
			
			t.start();
			
			
		}
		else if(e.getSource() == btnSelectMFolder)
		{
			System.out.println("Merging Files");
			selectedMergeFile = selectFile(false);
			
			if(selectedMergeFile == null)
				return;
			
			if(selectedMergeFile.isDirectory()){
				JOptionPane.showMessageDialog(frame, "Please select first splitted file to merge.");
			}	
			
			txtMFileSelection.setText(selectedMergeFile.getAbsolutePath());
			
			String fileName = selectedMergeFile.getName().substring(1);
			File parent = selectedMergeFile.getParentFile();
			int numFiles = 0;
			int i = 1;
			while((new File(parent,(i+1) + fileName )).exists())
			{
				i++;
				numFiles++;
			}
			
			txtSplitNum.setText(numFiles + "");
			txtMergeName.setText(fileName);
		}
		else if(e.getSource() == btnSelectMLocation)
		{
			System.out.println("Selecting  Mergre file location");
			selectedMergedFolder = selectFile(true);
			
			if(selectedMergedFolder == null)
				return;
			
			if(selectedMergedFolder.isFile()){
				JOptionPane.showMessageDialog(frame, "Please select a folder to split.");
			}	
			
			txtMergeLocation.setText(selectedMergedFolder.getAbsolutePath());
		}
		else if(e.getSource() == btnDoMerge)
		{
			System.out.println("Merging files.....");
			if(selectedMergeFile == null)
			{
				JOptionPane.showMessageDialog(frame, "Please select a first part of splitted files.");
				return;
			}
			
			if(selectedMergedFolder == null)
			{
				JOptionPane.showMessageDialog(frame, "Please select merge location.");
				return;
			}
			
			String mergeName = txtMergeName.getText().trim();
			if(mergeName.length() == 0)
			{
				JOptionPane.showMessageDialog(frame, "Please specify merge file name.");
				return;
			}
			
			Thread t = new Thread(){
				public void run()
				{
					FileSplitMerge fsm = new FileSplitMerge();
					fsm.mergeFiles(selectedMergeFile, new File(selectedMergedFolder, mergeName));
				}
			};
			
			t.start();
			
			
		}
	}
	
	private void renderFileMergePanel()
	{
		//====== Merge File Panel ================================================
		JPanel p1 = new JPanel();
		p1.setLayout(null);
		p1.setBounds(10,10,470, 300);
		
		p1.setBorder(BorderFactory.createTitledBorder("Merge Files"));
		
		mergePanel.add(p1);
		
		Label l1 = new Label("Please select first splitted file");
		l1.setBounds(15,20,400,30);
		p1.add(l1);
		
		btnSelectMFolder = new JButton("Browse...");
		btnSelectMFolder.setBounds(15, 50, 100, 30);
		btnSelectMFolder.addActionListener(this);
		p1.add(btnSelectMFolder);
		
		txtMFileSelection = new JTextField();
		txtMFileSelection.setBounds(120, 50, 330, 30);
		txtMFileSelection.setEditable(false);
		p1.add(txtMFileSelection);
		
		l1 = new Label("Total number of splitted files found");
		l1.setBounds(15,80,400,25);
		p1.add(l1);
		
		txtSplitNum = new JTextField();
		txtSplitNum.setBounds(15, 105, 435, 30);
		txtSplitNum.setEditable(false);
		p1.add(txtSplitNum);
		
		l1 = new Label("Please enter the name of merged file");
		l1.setBounds(15,135,400,25);
		p1.add(l1);
		
		txtMergeName = new JTextField();
		txtMergeName.setBounds(15, 160, 435, 30);
		p1.add(txtMergeName);
		
		l1 = new Label("Please select where merged file will be stored");
		l1.setBounds(15,190,400,25);
		p1.add(l1);
		
		btnSelectMLocation = new JButton("Browse...");
		btnSelectMLocation.setBounds(15, 215, 100, 30);
		btnSelectMLocation.addActionListener(this);
		p1.add(btnSelectMLocation);
		
		txtMergeLocation = new JTextField();
		txtMergeLocation.setBounds(120, 215, 330, 30);
		txtMergeLocation.setEditable(false);
		p1.add(txtMergeLocation);
		
		btnDoMerge = new JButton("Merge Files");
		btnDoMerge.setBounds(350, 250, 100,  30);
		btnDoMerge.addActionListener(this);
		p1.add(btnDoMerge);
	}
	
	private void renderFilePanel()
	{
		//====== Split File Panel ================================================
		JPanel p1 = new JPanel();
		p1.setLayout(null);
		p1.setBounds(10,10,470, 300);
		
		p1.setBorder(BorderFactory.createTitledBorder("Split Files"));
		
		splitPanel.add(p1);
		
		Label l1 = new Label("Please select a file to split");
		l1.setBounds(15,20,400,30);
		p1.add(l1);
		
		btnSelectFile = new JButton("Browse...");
		btnSelectFile.setBounds(15, 50, 100, 30);
		btnSelectFile.addActionListener(this);
		p1.add(btnSelectFile);
		
		txtFileSelection = new JTextField();
		txtFileSelection.setBounds(120, 50, 330, 30);
		txtFileSelection.setEditable(false);
		p1.add(txtFileSelection);
		
		l1 = new Label("Selected File Size (In Bytes)");
		l1.setBounds(15,80,400,25);
		p1.add(l1);
		
		txtFileSize = new JTextField();
		txtFileSize.setBounds(15, 105, 435, 30);
		txtFileSize.setEditable(false);
		p1.add(txtFileSize);
		
		l1 = new Label("Please enter the size of splitted file (In Bytes)");
		l1.setBounds(15,135,400,25);
		p1.add(l1);
		
		txtSplitSize = new JTextField();
		txtSplitSize.setBounds(15, 160, 435, 30);
		p1.add(txtSplitSize);
		
		l1 = new Label("Please select location to split");
		l1.setBounds(15,190,400,25);
		p1.add(l1);
		
		btnSelectSplitFolder = new JButton("Browse...");
		btnSelectSplitFolder.setBounds(15, 215, 100, 30);
		btnSelectSplitFolder.addActionListener(this);
		p1.add(btnSelectSplitFolder);
		
		txtSplitFileLocation = new JTextField();
		txtSplitFileLocation.setBounds(120, 215, 330, 30);
		txtSplitFileLocation.setEditable(false);
		p1.add(txtSplitFileLocation);
		
		btnDoSplit = new JButton("Split File");
		btnDoSplit.setBounds(350, 250, 100,  30);
		btnDoSplit.addActionListener(this);
		p1.add(btnDoSplit);
	}
	
	private void renderZipPanel()
	{
		//======= Zip Panel =======================================================
		JPanel p1 = new JPanel();
		p1.setLayout(null);
		p1.setBounds(10,10,470, 130);
		
		p1.setBorder(BorderFactory.createTitledBorder("Zip Files"));
		
		zipPanel.add(p1);
		
		Label l1 = new Label("Please select files/folder for zip");
		l1.setBounds(15,20,400,30);
		p1.add(l1);
		
		btnSelectZip = new JButton("Browse...");
		btnSelectZip.setBounds(15, 50, 100, 30);
		btnSelectZip.addActionListener(this);
		p1.add(btnSelectZip);
		
		txtZipSelection = new JTextField();
		txtZipSelection.setBounds(120, 50, 330, 30);
		txtZipSelection.setEditable(false);
		p1.add(txtZipSelection);
		
		btnDoZip = new JButton("Zip");
		btnDoZip.setBounds(350, 80, 100,  30);
		btnDoZip.addActionListener(this);
		p1.add(btnDoZip);
		
		
		//===== Un-Zip Panel ====================================================
		p1 = new JPanel();
		p1.setLayout(null);
		p1.setBounds(10,150,470, 130);
		
		p1.setBorder(BorderFactory.createTitledBorder("Un-Zip Files"));
		
		zipPanel.add(p1);
		
		l1 = new Label("Please select a zip file to Un-Zip");
		l1.setBounds(15,20,400,30);
		p1.add(l1);
		
		btnSelectUnzip = new JButton("Browse...");
		btnSelectUnzip.setBounds(15, 50, 100, 30);
		btnSelectUnzip.addActionListener(this);
		p1.add(btnSelectUnzip);
		
		txtUnZipSelection = new JTextField();
		txtUnZipSelection.setBounds(120, 50, 330, 30);
		txtUnZipSelection.setEditable(false);
		p1.add(txtUnZipSelection);
		
		btnDoUnZip = new JButton("Un-Zip");
		btnDoUnZip.setBounds(350, 80, 100,  30);
		btnDoUnZip.addActionListener(this);
		p1.add(btnDoUnZip);
	}
	
	private File selectFile(boolean dir_only)
	{
		File selectedFile = null;
		JFileChooser jfc = new JFileChooser();
		
		if(dir_only)
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		else
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		int action = jfc.showOpenDialog(frame);
		if(action == jfc.APPROVE_OPTION)
		{
			selectedFile = jfc.getSelectedFile();
		}
		
		return selectedFile;
	}
	
	private File[] selectFiles(boolean dir_only)
	{
		File selectedFile[] = null;
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(true);
		if(dir_only)
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		else
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		int action = jfc.showOpenDialog(frame);
		if(action == jfc.APPROVE_OPTION)
		{
			selectedFile = jfc.getSelectedFiles();
		}
		
		return selectedFile;
	}
	
	public static void main(String args[])
	{
		try{
			UIManager.setLookAndFeel(new javax.swing.plaf.nimbus.NimbusLookAndFeel());
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e){}
		new Main();
	}
}