package cn.easyjce.plugin.utils;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.Objects;

/**
 * @Class: FileChooserUtil
 * @Date: 2022/8/12 16:50
 * @author: cuijiufeng
 */
public class FileChooserUtil {

    public static byte[] byteContentFilechooser(FileChooserDescriptor descriptor, Project project, VirtualFile toSelect) {
        try {
            return Objects.requireNonNull(FileChooser.chooseFile(descriptor, project, toSelect)).contentsToByteArray();
        } catch (IOException e) {
            LogUtil.LOG.error(e);
            return null;
        }
    }

    public static class ChooserDescBuilder {
        private boolean chooseFiles = false;
        private boolean chooseFolders = false;
        private boolean chooseJars = false;
        private boolean chooseJarsAsFiles = false;
        private boolean chooseJarContents = false;
        private boolean chooseMultiple = false;

        public ChooserDescBuilder setChooseFiles(boolean chooseFiles) {
            this.chooseFiles = chooseFiles;
            return this;
        }

        public ChooserDescBuilder setChooseFolders(boolean chooseFolders) {
            this.chooseFolders = chooseFolders;
            return this;
        }

        public ChooserDescBuilder setChooseJars(boolean chooseJars) {
            this.chooseJars = chooseJars;
            return this;
        }

        public ChooserDescBuilder setChooseJarsAsFiles(boolean chooseJarsAsFiles) {
            this.chooseJarsAsFiles = chooseJarsAsFiles;
            return this;
        }

        public ChooserDescBuilder setChooseJarContents(boolean chooseJarContents) {
            this.chooseJarContents = chooseJarContents;
            return this;
        }

        public ChooserDescBuilder setChooseMultiple(boolean chooseMultiple) {
            this.chooseMultiple = chooseMultiple;
            return this;
        }

        public FileChooserDescriptor build() {
            return new FileChooserDescriptor(chooseFiles, chooseFolders, chooseJars, chooseJarsAsFiles, chooseJarContents, chooseMultiple);
        }
    }
}
