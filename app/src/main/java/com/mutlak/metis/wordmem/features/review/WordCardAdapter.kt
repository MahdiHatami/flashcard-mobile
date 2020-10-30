package com.mutlak.metis.wordmem.features.review

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.gson.Gson
import com.mutlak.metis.wordmem.data.model.Word


class WordCardAdapter(val words: List<Word>, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {


  override fun getItem(position: Int): Fragment {
    val current = words[position]
    return if (current.english.isEmpty()) {
      TakeQuizFragment.create()
    } else {
      val s = Gson().toJson(current)
      CardFragment.create(s)
    }
  }

  override fun getCount(): Int {
    return words.size
  }

}
