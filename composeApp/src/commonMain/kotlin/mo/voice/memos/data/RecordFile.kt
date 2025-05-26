package mo.voice.memos.data

import kotlinx.io.files.Path
import mo.voice.memos.data.database.entities.tag.Tag

data class RecordFile(val path: Path, val name: String,val tag: Tag?=null)
