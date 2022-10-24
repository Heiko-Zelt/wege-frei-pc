package de.heikozelt.wegefrei

import org.junit.jupiter.api.Test
import javax.swing.UIManager


class LookAndFeelTest {

    /**
     * <table>
     *   <tr><th>Name</th>     <th>Class name</th>                                    <td>on Ubuntu</td></tr>
     *   <tr><td>Metal</td>    <td>javax.swing.plaf.metal.MetalLookAndFeel</td>       <td>CrossPlattform</td></tr>
     *   <tr><td>Nimbus</td>   <td>javax.swing.plaf.nimbus.NimbusLookAndFeel</td>     <td>3D bubble style</td></tr>
     *   <tr><td>CDE/Motif</td><td>com.sun.java.swing.plaf.motif.MotifLookAndFeel</td><td>old fashioned</td></tr>
     *   <tr><td>GTK+</td>     <td>com.sun.java.swing.plaf.gtk.GTKLookAndFeel</td>    <td>System</td></tr>
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
    fun listLookAndFeels_auxilaries() {
        val lookAndFeels = UIManager.getAuxiliaryLookAndFeels()
        if(lookAndFeels == null) {
            println("null")
        } else {
            for (lookAndFeel in lookAndFeels) {
                println("aux: ${lookAndFeel.name}      ${lookAndFeel::class.java.canonicalName}");
            }
        }
    }

    @Test
    fun listLookAndFeels_cross_and_system() {
        var lookAndFeelClassName = UIManager.getCrossPlatformLookAndFeelClassName()
        println("cross: $lookAndFeelClassName")

        lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName()
        println("system: $lookAndFeelClassName")
    }

    @Test
    fun listLookAndFeels_names_functional() {
        val lookAndFeels = UIManager.getInstalledLookAndFeels().map { it.name }
        lookAndFeels.forEach { println(it) }
    }
}