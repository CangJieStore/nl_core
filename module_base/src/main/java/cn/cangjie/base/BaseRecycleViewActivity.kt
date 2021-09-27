package cn.cangjie.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cangjie.base.databinding.ActivityBaseRecylceviewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

abstract class BaseRecycleViewActivity<T> : AppCompatActivity() {

    private lateinit var binding: ActivityBaseRecylceviewBinding
    private lateinit var baseAdapter: BaseQuickAdapter<T, BaseViewHolder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseRecylceviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        when (gridCount()) {
            0 -> binding.ryBase.layoutManager = LinearLayoutManager(this)
            else -> binding.ryBase.layoutManager = GridLayoutManager(this, gridCount())
        }
        baseAdapter = setAdapter()
        binding.ryBase.adapter = baseAdapter
        getHeaderView()?.let {
            baseAdapter.addHeaderView(it)
        }
    }


    abstract fun gridCount(): Int
    abstract fun setAdapter(): BaseQuickAdapter<T, BaseViewHolder>
    abstract fun setData(): MutableList<T>
    fun getData(): List<T> = baseAdapter.data
    fun getAdapter(): BaseQuickAdapter<T, BaseViewHolder> = baseAdapter

    fun getRecycleView(): RecyclerView = binding.ryBase
    abstract fun getHeaderView(): View?


}