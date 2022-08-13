package cn.easyjce.plugin.beans;

import cn.easyjce.plugin.utils.FileChooserUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

/**
 * @Class: JButtonParameter
 * @Date: 2022/8/12 16:41
 * @author: cuijiufeng
 */
public class JButtonParameter extends Parameter<byte[]> {
    private final JButton button;
    private byte[] file;

    public JButtonParameter(String label, String btnLabel) {
        super(label, 1, () -> true);
        this.button = new JButton(btnLabel);
        this.button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                file = FileChooserUtil.byteContentFilechooser(
                        new FileChooserUtil.ChooserDescBuilder().setChooseFiles(true).build(),
                        null,
                        null);
            }
        });
    }

    @Override
    public byte[] getValue() {
        return this.file;
    }

    @Override
    public List<? extends JComponent> getComponent() {
        return Collections.singletonList(button);
    }
}
