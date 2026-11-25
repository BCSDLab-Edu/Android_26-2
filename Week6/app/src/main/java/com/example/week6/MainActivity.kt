package com.example.week6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week6.ui.theme.Week6Theme

// 각 항목의 고유 ID와 이름
data class NameItem(
    val id: Long,
    val name: String
)

// 화면 회전 시 목록 저장 및 복원
private val NameListSaver = listSaver<List<NameItem>, Any>(
    save = { names ->
        names.flatMap { listOf(it.id, it.name) }
    },
    restore = { saved ->
        saved.chunked(2).map {
            NameItem(it[0] as Long, it[1] as String)
        }
    }
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Week6Theme {
                NameScreen()
            }
        }
    }
}

// 상태를 소유하고 관리하는 부모 Composable
@Composable
fun NameScreen() {

    var names by rememberSaveable(
        stateSaver = NameListSaver
    ) {
        mutableStateOf<List<NameItem>>(emptyList())
    }

    var input by rememberSaveable {
        mutableStateOf("")
    }

    // 새로운 항목에 부여할 고유 ID
    var nextId by rememberSaveable {
        mutableStateOf(0L)
    }

    var deleteId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var editId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var editInput by rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color(0xFFFFC107),
                contentColor = Color.Black,
                onClick = {
                    val name = input.trim()

                    if (name.isNotEmpty()) {
                        names = names + NameItem(
                            id = nextId,
                            name = name
                        )

                        nextId++
                        input = ""
                    }
                }
            ) {
                Text("+", fontSize = 28.sp)
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            NameInput(
                value = input,
                onValueChange = { input = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NameList(
                names = names,
                onDelete = { item ->
                    deleteId = item.id
                },
                onEdit = { item ->
                    editId = item.id
                    editInput = item.name
                }
            )
        }
    }

    // 삭제 다이얼로그
    val deleteItem = names.find { it.id == deleteId }

    if (deleteItem != null) {
        AlertDialog(
            onDismissRequest = {
                deleteId = null
            },
            title = {
                Text("이름 목록 삭제하기")
            },
            text = {
                Text("${deleteItem.name}을(를) 삭제하시겠습니까?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        names = names.filterNot {
                            it.id == deleteItem.id
                        }
                        deleteId = null
                    }
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deleteId = null }
                ) {
                    Text("취소")
                }
            }
        )
    }

    // 수정 다이얼로그
    val editItem = names.find { it.id == editId }

    if (editItem != null) {
        AlertDialog(
            onDismissRequest = {
                editId = null
                editInput = ""
            },
            title = {
                Text("이름 수정하기")
            },
            text = {
                OutlinedTextField(
                    value = editInput,
                    onValueChange = { editInput = it },
                    label = {
                        Text("수정할 이름")
                    },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    enabled = editInput.isNotBlank(),
                    onClick = {
                        val newName = editInput.trim()

                        names = names.map { item ->
                            if (item.id == editItem.id) {
                                item.copy(name = newName)
                            } else {
                                item
                            }
                        }

                        editId = null
                        editInput = ""
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        editId = null
                        editInput = ""
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

// 상태를 전달받는 입력창
@Composable
fun NameInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("새로운 이름을 추가하세요")
        },
        singleLine = true
    )
}

// 상태를 전달받는 목록
@Composable
fun NameList(
    names: List<NameItem>,
    onDelete: (NameItem) -> Unit,
    onEdit: (NameItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = names,
            key = { it.id },
            contentType = { "name" }
        ) { item ->
            NameRow(
                item = item,
                onClick = { onDelete(item) },
                onLongClick = { onEdit(item) }
            )
        }
    }
}

// 목록의 개별 항목
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NameRow(
    item: NameItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Text(
        text = item.name,
        color = Color.White,
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFF9800))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(14.dp)
    )
}