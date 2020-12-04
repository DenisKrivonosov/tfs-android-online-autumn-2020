package ru.krivonosovdenis.fintechapp.presentation.appsettings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.krivonosovdenis.fintechapp.ApplicationClass
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import javax.inject.Inject


class AppSettingsFragment : PreferenceFragmentCompat(), AppSettingsView  {

    @Inject
    @InjectPresenter
    lateinit var presenter: AppSettingsPresenter

    @ProvidePresenter
    fun provide() = presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.applicationContext as ApplicationClass).addAppSettingsComponent()
        (activity?.applicationContext as ApplicationClass).appSettingsComponent?.inject(this)
        (activity as MainActivity).hideBottomSettings()
    }

    override fun onDetach() {
        (activity?.applicationContext as ApplicationClass).clearAppSettingsComponent()
        super.onDetach()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferencesscreen, rootKey)
    }

    override fun setPreferenceScreen(preferenceScreen: PreferenceScreen?) {
        super.setPreferenceScreen(preferenceScreen)
        val outerSettingsPref = findPreference("outerSettings") as Preference?
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            outerSettingsPref?.isVisible = false
        } else {
            outerSettingsPref?.setOnPreferenceClickListener {
                Log.e("CLIICKED", "TRUUU");
//                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
//                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
                false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    companion object {
        fun newInstance(): AppSettingsFragment {
            return AppSettingsFragment()
        }
    }
}
