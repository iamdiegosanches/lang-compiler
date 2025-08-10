package langtester;


import java.util.List;
import java.util.LinkedList;
import java.io.*;


public class InstanceRepository {
     private int passed, size;
     private short tag;
     private boolean multiLine;
     private boolean inputDependent;
     private String description, path, ext, expectedOutput;
     private LinkedList<File> files;
     private LinkedList<InstanceResult> fails;


      public InstanceRepository(short tag, String desc, String path, String ext, String expectedOutput, boolean multiLine, boolean inputDependent) throws IOException {
           passed = 0;
           this.tag = tag;
           this.path = path;
           this.ext = ext;
           this.multiLine  = inputDependent ? false : multiLine;
           this.inputDependent = inputDependent;
           this.expectedOutput = expectedOutput;
           description = desc;
           files = new LinkedList<File>();
           fails = new LinkedList<InstanceResult>();
           size = list_files(path, ext).length;
      }

      private File[] list_files(String path, String ext) throws IOException{
         File fs = new File(path);
         FileFilter ff = new FileFilter(){
               public boolean accept(File f){
                    return f.isFile() && f. getName().endsWith("."+ext);
               }
         };
         return fs.listFiles(ff);
      }

      public void loadInstances() throws IOException{
           files.clear();
           File[] arr = list_files(path, ext);
           for(File f : arr){
              files.add(f);
           }
      }

      public File current(){ return files.peek();}

      public void acceptCurrent(){
          passed++;
          files.removeFirst();
      }

      public void rejectCurrent(){
           InstanceResult r = new InstanceResult();
           r.fname = files.removeFirst().getPath();
           r.fails = null;
           fails.add(r);
      }

      public void rejectCurrent(LinkedList<TestInstance> l){
           InstanceResult r = new InstanceResult();
           r.fname = files.removeFirst().getPath();
           r.fails = l;
           fails.add(r);
      }

      public int rejected(){ return fails.size(); }

      public int passed(){ return passed; }

      public int size(){ return size; }

      public String getDescription(){
           return description;
      }

      public String getPath(){ return path; }

      public LinkedList<InstanceResult> getFails(){ return fails; }

      public short getTag(){return tag;}

      public boolean hasNext(){return files.size() > 0; }

      public int processedFiles(){return size - files.size(); }

      public String getExpectedOutput() { return expectedOutput; }

      public boolean isInputDependent() { return inputDependent; }

      public boolean isMultiLine() { return multiLine; }


   }
