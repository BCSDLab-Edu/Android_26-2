package com.example.week4


    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView


    class NameAdapter(
        private val names: MutableList<String>,
        private val onItemClick: (Int) -> Unit,
        private val onItemLongClick: (Int) -> Unit
    ) : RecyclerView.Adapter<NameAdapter.NameViewHolder>() {

        class NameViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

            val textView: TextView =
                itemView.findViewById(R.id.textViewName)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): NameViewHolder {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_name, parent, false)

            return NameViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: NameViewHolder,
            position: Int
        ) {
            holder.textView.text = names[position]

            holder.itemView.setOnClickListener {
                val index = holder.bindingAdapterPosition
                if (index != RecyclerView.NO_POSITION) {
                    onItemClick(index)
                }
            }

            holder.itemView.setOnLongClickListener {
                val index = holder.bindingAdapterPosition
                if (index != RecyclerView.NO_POSITION) {
                    onItemLongClick(index)
                }
                true
            }
        }

        override fun getItemCount(): Int = names.size
    }

