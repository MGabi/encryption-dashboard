package com.example

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.treksoft.kvision.core.onEvent
import pl.treksoft.kvision.form.FormPanel
import pl.treksoft.kvision.form.check.CheckBox
import pl.treksoft.kvision.form.formPanel
import pl.treksoft.kvision.form.select.SelectRemote
import pl.treksoft.kvision.form.text.Text
import pl.treksoft.kvision.html.ButtonStyle
import pl.treksoft.kvision.html.button
import pl.treksoft.kvision.i18n.I18n.tr
import pl.treksoft.kvision.panel.HPanel
import pl.treksoft.kvision.panel.StackPanel
import pl.treksoft.kvision.utils.ENTER_KEY
import pl.treksoft.kvision.utils.px

object EditPanel : StackPanel() {
    private val formPanel: FormPanel<ApiKey>

    init {
        padding = 10.px

        formPanel = formPanel {
            add(
                ApiKey::name,
                Text(label = "${tr("Api key name")}:"),
                required = true
            )
            add(
                ApiKey::type,
                SelectRemote(
                    ApiKeysServiceManager,
                    function = IApiKeysService::getEncryptionTypes,
                    label = "Select encryption type"
                ),
                required = true
            )

            add(ApiKey::favourite, CheckBox(label = tr("Mark as favourite")))
            add(HPanel(spacing = 10) {
                button(tr("Save"), "fas fa-check", ButtonStyle.PRIMARY).onClick {
                    save()
                }
                button(tr("Cancel"), "fas fa-times", ButtonStyle.SECONDARY).onClick {
                    close()
                }
            })
            onEvent {
                keydown = {
                    if (it.keyCode == ENTER_KEY) {
                        save()
                    }
                }
            }
        }
        add(MainPanel)
    }

    fun add() {
        formPanel.clearData()
        open()
    }

    private fun save() {
        GlobalScope.launch {
            if (formPanel.validate()) {
                val apiKey = formPanel.getData()
                Model.addApiKey(apiKey)
                close()
            }
        }
    }

    fun delete(index: Int) {
        GlobalScope.launch {
            close()
            Model.apiKeys[index].id?.let {
                Model.deleteApiKey(it)
            }
        }
    }

    private fun open() {
        activeChild = formPanel
        formPanel.getControl(ApiKey::key)?.focus()
    }

    private fun close() {
        activeChild = MainPanel
    }
}
