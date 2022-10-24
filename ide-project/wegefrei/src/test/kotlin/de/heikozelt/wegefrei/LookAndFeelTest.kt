package de.heikozelt.wegefrei

import org.junit.jupiter.api.Test
import javax.swing.UIManager


class LookAndFeelTest {

    /**
     * <table>
     *   <tr><th>Name</th>     <th>Class name</th></tr>
     *   <tr><td>Metal</td>    <td>javax.swing.plaf.metal.MetalLookAndFeel</td></tr>
     *   <tr><td>Nimbus</td>   <td>javax.swing.plaf.nimbus.NimbusLookAndFeel</td></tr>
     *   <tr><td>CDE/Motif</td><td>com.sun.java.swing.plaf.motif.MotifLookAndFeel</td></tr>
     *   <tr><td>GTK+</td>     <td>com.sun.java.swing.plaf.gtk.GTKLookAndFeel</td></tr>
     * </table>
     */
    @Test
    fun listLookAndFeels_imperative() {
        val lookAndFeels = UIManager.getInstalledLookAndFeels()
        for (lookAndFeel in lookAndFeels) {
            println("${lookAndFeel.name}      ${lookAndFeel.className}");
        }
    }

    @Test
    fun listLookAndFeels_names_functional() {
        val lookAndFeels = UIManager.getInstalledLookAndFeels().map { it.name }
        lookAndFeels.forEach { println(it) }
    }
}