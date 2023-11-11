package Demo;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;

public class FileDragAndDropExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("File Drag and Drop Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new FileDropPanel();
        frame.add(panel);

        frame.setVisible(true);
    }
}

class FileDropPanel extends JPanel {
    private DefaultListModel<String> fileListModel;
    private JList<String> fileList;

    public FileDropPanel() {
        setLayout(new BorderLayout());

        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);

        JScrollPane scrollPane = new JScrollPane(fileList);
        add(scrollPane, BorderLayout.CENTER);

        FileDropTargetAdapter fileDropTarget = new FileDropTargetAdapter(this);
        new DropTarget(this, DnDConstants.ACTION_COPY, fileDropTarget, true);

        setTransferHandler(new FileTransferHandler());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

    private class FileDropTargetAdapter extends DropTargetAdapter {
        private JPanel panel;

        public FileDropTargetAdapter(JPanel panel) {
            this.panel = panel;
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                Transferable transferable = dtde.getTransferable();
                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    java.util.List<File> fileList = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

                    for (File file : fileList) {
                        // Handle the dropped files (e.g., display their names)
                        fileListModel.addElement(file.getName());
                    }

                    dtde.dropComplete(true);
                } else {
                    dtde.rejectDrop();
                }
            } catch (Exception e) {
                e.printStackTrace();
                dtde.rejectDrop();
            }
        }
    }

    private class FileTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (canImport(support)) {
                Transferable transferable = support.getTransferable();
                try {
                    java.util.List<File> fileList = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
 
                    for (File file : fileList) {
                        // Handle the dropped files (e.g., display their names)
                        fileListModel.addElement(file.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
            return false;
        }
    }
}
