package Server.File_Transfer_Server;

import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDirClient {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    private ObjectOutputStream out;
    private int PORT;
	private ObjectInputStream in;
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
     
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    /**
     * 
     */
    
 
    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDirClient(Path dir, boolean recursive, OutputStream out, InputStream in, int PORT) throws IOException {
    	this.PORT = PORT;
    	this.out = new ObjectOutputStream(out);
    	this.in = new ObjectInputStream(in);
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.recursive = recursive;

        
        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

//            for (WatchEvent<?> event: key.pollEvents()) {
//                WatchEvent.Kind kind = event.kind();
//
//                // TBD - provide example of how OVERFLOW event is handled
//                if (kind == OVERFLOW) {
//                    continue;
//                }
//
//                // Context for directory entry event is the file name of entry
//                WatchEvent<Path> ev = cast(event);
//                Path name = ev.context();
//                Path child = dir.resolve(name);
//
//                // print out event
//                System.out.format("%s: %s\n", event.kind().name(), child);
//
//                // if directory is created, and watching recursively, then
//                // register it and its sub-directories
//                if (recursive && (kind == ENTRY_CREATE)) {
//                    try {
//                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
//                            registerAll(child);
//                        }
//                        else register(child);
//                        out.writeObject(new FileMetaData(kind, child.toString(), -1));
//                    } catch (IOException x) {
//                        // ignore to keep sample readbale
//                    }
//                }
//                else if(kind ==ENTRY_DELETE) {
//                	
//                }
//                else if(kind ==ENTRY_MODIFY) {
//                	
//                }
//                
//            }
            
            List<WatchEvent<?>> events =  key.pollEvents();
            WatchEvent<?> event = events.get(0);
            Kind<?> kind = event.kind();
            WatchEvent<Path> ev = cast(event);
            Path name = ev.context();
            Path child = dir.resolve(name);

            // print out event
            System.out.format("%s: %s\n", event.kind().name(), child);

            // if directory is created, and watching recursively, then
            // register it and its sub-directories
            if(Files.isRegularFile(child, NOFOLLOW_LINKS)) {
            	 if (recursive && (kind == ENTRY_CREATE)) {
                     try {
                         if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                             registerAll(child);
                             out.writeObject(new FileMetaData(kind, child.toString(), -1));                             
                             createDirOnRemote(child.toFile(), kind);
                         }
                         else {
                        	 register(child);
                             out.writeObject(new FileMetaData(kind, child.toString(), child.toFile().length()));
                             in.readObject();
                             FileReader reader = new FileReader(new FileSender(PORT), child.toString());
                             reader.read();

                         }
                     } catch (IOException | ClassNotFoundException x) {
                         // ignore to keep sample readbale
                     }
                 }
                 else if(kind ==ENTRY_DELETE) {
                     try {
     					out.writeObject(new FileMetaData(kind, child.toString(), -1));
     				} catch (IOException e) {
     					// TODO Auto-generated catch block
     					e.printStackTrace();
     				}

                 }
                 else if(kind ==ENTRY_MODIFY) {
                 	 try {
                         out.writeObject(new FileMetaData(kind, child.toString(), child.toFile().length()));
                         in.readObject();
                         FileReader reader = new FileReader(new FileSender(PORT), child.toString());
                         reader.read();
      				} catch (IOException | ClassNotFoundException e) {
      					e.printStackTrace();
      				}
                 }
            }
           
            
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
            
        }
    }
    private void createDirOnRemote(File child, Kind<?> kind) {
    	ArrayList<File> directories = new ArrayList<>();
    	File[] files = child.listFiles();
    	for(File curFile : files) {
			if(curFile.isDirectory()) {
				directories.add(curFile);
                try {
					out.writeObject(new FileMetaData(kind, child.toString(), -1));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{

				FileReader reader;
				try {
	                out.writeObject(new FileMetaData(kind, curFile.toString(), curFile.length()));
	                in.readObject();
					reader = new FileReader(new FileSender(PORT), child.toString());
			    	reader.read();

				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}
    	for(File dir : directories) {
    		createDirOnRemote(dir, kind);
    	}
    }
//    static void usage() {
//        System.err.println("usage: java WatchDir [-r] dir");
//        System.exit(-1);
//    }
//
//    public static void main(String[] args) throws IOException {
//        // parse arguments
//        if (args.length == 0 || args.length > 2)
//            usage();
//        boolean recursive = false;
//        int dirArg = 0;
//        if (args[0].equals("-r")) {
//            if (args.length < 2)
//                usage();
//            recursive = true;
//            dirArg++;
//        }
//
//        // register directory and process its events
//        Path dir = Paths.get(args[dirArg]);
//        new WatchDir(dir, recursive).processEvents();
//    }
}
