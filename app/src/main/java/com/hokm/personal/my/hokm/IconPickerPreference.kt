package com.hokm.personal.my.hokm

import android.preference.ListPreference
import android.app.AlertDialog
import android.content.Context
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.hokm.personal.my.hokm.util.PrefsHelper


class IconPickerPreference : ListPreference {

    inner class CustomListPreferenceAdapter: ArrayAdapter<String> {

     var resource: Int = 0
        var values = listOf<String>()
        constructor(context: Context,
                     resource: Int,
                     values: List<String>): super(context, resource, values){
            this.resource = resource
            this.values = values
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var holder: ViewHolder
            var view: View? = convertView

            if (view == null) {
                val inflater = context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(resource, parent, false)

                holder = ViewHolder(values[position], view.findViewById(R.id.img_background) as ImageView)

                view.tag = holder

                view?.setOnClickListener{
                    val holder = it.getTag() as ViewHolder
                    selectedIconFile = holder.iconName
                    dialog.dismiss()
                }
            } else {
                holder = view.tag as ViewHolder
            }

            val identifier = context.resources.getIdentifier(
                    values[position], "drawable",
                    context.packageName)
            holder.iconImage.setImageResource(identifier)
            //holder.iconImage.tag = values[position]




            return view
        }

    }

     var selectedIconFile = ""
    lateinit var icon: ImageView
    var values = listOf<String>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    override fun onBindView(view: View?) {
        super.onBindView(view)
        icon = view?.findViewById(R.id.iconSelected) as ImageView
        selectedIconFile = PrefsHelper.getBackgroundIcon()
                //preferences?.getString(
                //context.resources.getString(R.string.custom_icon_key), "background") ?: "background"
        updateIcon()
    }

    private fun updateIcon() {
        val identifier = context.resources.getIdentifier(selectedIconFile, "drawable",
                context.packageName)

        icon.setImageResource(identifier)
        icon.setTag(selectedIconFile)
    }

    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder?) {
        builder?.setNegativeButton(null, null)
        builder?.setPositiveButton(null, null)
        builder?.setTitle("")

        val icons = listOf("background", "background2")
        val customListPreferenceAdapter = CustomListPreferenceAdapter(
                context, R.layout.item_game_background, icons)
        builder?.setAdapter(customListPreferenceAdapter, null)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)
        PrefsHelper.saveBackgroundIcon(selectedIconFile)
//        val editor = preferences?.edit()
//        editor?.putString(
//                context.resources.getString(R.string.custom_icon_key),
//                selectedIconFile)
//        editor?.commit()
        updateIcon()
    }

}


data class ViewHolder(val iconName: String, val iconImage: ImageView)

