package com.example.yeniproje

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class FavouriteFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Layout dosyasını buraya bağlayın
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }
}
