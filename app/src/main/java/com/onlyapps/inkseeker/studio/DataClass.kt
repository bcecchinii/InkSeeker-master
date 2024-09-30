package com.onlyapps.inkseeker.studio

import org.json.JSONArray

data class StyleDataClass(var text: String) {
}

data class TatooerDataClass(var id: Int, var name: String, var picLink: String, var instagramTag: String, var instagramLink: String , var styles: JSONArray) {
}