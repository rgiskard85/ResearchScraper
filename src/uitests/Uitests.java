
package uitests;
/**
 *
 * @author giskard
 */

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
public class Uitests {
    
    public static void main(String[] args) {
        UI ui;
        try {
            ui = new UI();
            ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ui.setSize(600, 800);
            ui.setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Uitests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
