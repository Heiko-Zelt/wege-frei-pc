package de.heikozelt.wegefrei.dirnavi

import org.slf4j.LoggerFactory
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.*

class DirectoryNavigation(private val directoryChangedCallback: (AbsolutePath) -> Unit): JPanel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var path: AbsolutePath? = null
    private val buttons = mutableListOf<JButton>()
    private var label = JLabel()
    private var comboModel = DefaultComboBoxModel<String>()
    private var comboBox = JComboBox(comboModel)
    private val comboListener = ItemListener { event ->
        if(event?.stateChange == ItemEvent.SELECTED) {
            val item = event.item
            log.debug("selected item: $item")
            if(item is String && item != "") {
                enterSubDirectory(item)
            }
        }
    }

    private class ButListener(private val navi: DirectoryNavigation, private val level: Int): ActionListener {
        override fun actionPerformed(event: ActionEvent?) {
            navi.changeDirectoryToLevel(level)
        }
    }

    init {
        //default: java.awt.FlowLayout[hgap=5,vgap=5,align=center]
        //log.debug("layout: $layout")
        layout = FlowLayout(FlowLayout.LEFT, 0, 0)
    }

    private fun updateLayout() {
        revalidate()
        maximumSize = preferredSize
        repaint()
    }

    fun setDirectory(path: AbsolutePath) {
        log.debug("setDirectory(path=$path)")
        this.path = path
        updateButtonsAndLabel()
        updateComboBox()
        removeAll()
        addButtons()
        add(label)
        add(comboBox)
        updateLayout()
    }

    private fun addButtons() {
        for((level, but) in buttons.withIndex()) {
            add(but)
            but.addActionListener(ButListener(this, level))
        }
    }

    private fun updateButtonsAndLabel() {
        path?.let {p ->
            buttons.clear()
            val pathIter = p.iterator()
            for (element in pathIter) {
                if (pathIter.hasNext()) {
                    val but = JButton(element)
                    buttons.add(but)
                } else {
                    label.text = element
                }
            }
        }
    }

    private fun updateComboBox() {
        path?.let { p ->
            comboBox.removeItemListener(comboListener)
            val dirs = p.subDirectories()
            comboBox.isVisible = dirs.isNotEmpty()
            fillComboBox(dirs)
            comboBox.addItemListener(comboListener)
        }
    }

    private fun fillComboBox(dirs: List<String>) {
         comboModel.removeAllElements()
         comboModel.addElement("")
         dirs.forEach( comboModel::addElement )
    }

    private fun changeDirectoryToLevel(level: Int) {
        log.debug("changeDirectoryToLevel(level=$level)")
        label.text = buttons[level].text // index out of bounds
        for(i in level until buttons.size) {
            remove(buttons[i])
        }
        for(i in level until buttons.size) {
            buttons.removeLast()
        }
        log.debug("buttons.size=${buttons.size}")
        path?.truncate(level + 1)
        log.debug("new path=$path")
        updateComboBox()
        updateLayout()
        path?.let {
            directoryChangedCallback(it)
        }
    }

    /**
     * todo mit Windows testen, Laufwerksbuchstaben?
     */

    /**
     * ein Verzeichnis tiefer gehen
     */
    private fun enterSubDirectory(directoryName: String) {
        log.debug("enterSubDirectory(directoryName=$directoryName)")
        log.debug("old path: $path")
        val newBut = JButton(label.text)
        val level = buttons.size
        newBut.addActionListener(ButListener(this, level))
        /* implizite Version mit capture
        newBut.addActionListener {
            changeDirectoryToLevel(level)
        }
        */
        add(newBut, buttons.size)
        buttons.add(newBut)
        log.debug("buttons.size=${buttons.size}")
        label.text = directoryName
        path?.append(directoryName)
        log.debug("new path: $path")
        updateComboBox()
        updateLayout()
        path?.let {
            directoryChangedCallback(it)
        }
    }
}