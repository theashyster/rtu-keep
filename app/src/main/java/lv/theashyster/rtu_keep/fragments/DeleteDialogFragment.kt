package lv.theashyster.rtu_keep.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import lv.theashyster.rtu_keep.R

class DeleteDialogFragment : DialogFragment() {

    private lateinit var alertDialog: AlertDialog

    var action: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.fragment_delete_title))
            .setMessage(getString(R.string.fragment_delete_message))
            .setPositiveButton(getString(R.string.fragment_delete_ok)) { _, _ -> action?.invoke() }
            .setNegativeButton(getString(R.string.fragment_delete_cancel)) { _, _ -> }
            .create()

        return alertDialog
    }

    override fun onStop() {
        super.onStop()
        alertDialog.dismiss()
    }
}
