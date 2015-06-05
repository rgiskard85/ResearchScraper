
package uitests;
/**
 *
 * @author Dionysios-Charalampos Vythoulkas <dcvythoulkas@gmail.com>
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
            ui.setSize(800, 700);
            ui.setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Uitests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
