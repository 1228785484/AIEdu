package ${package.Controller};

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.sevengod.javabe.common.AjaxResult;
import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};

/**
 * ${table.comment!} 前端控制器
 *
 * @author ${author}
 * @since ${date}
 */
@RestController
@RequestMapping("/${table.entityPath}")
public class ${table.controllerName} {

    @Resource
    private ${table.serviceName} ${table.entityPath}Service;

    /**
     * 新增数据
     *
     * @param ${table.entityPath} ${table.comment!}对象
     * @return 新增结果
     */
    @PostMapping
    public AjaxResult add(@RequestBody ${entity} ${table.entityPath}) {
        return ${table.entityPath}Service.save(${table.entityPath}) 
            ? AjaxResult.success("添加成功", ${table.entityPath})
            : AjaxResult.error("添加失败");
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        return ${table.entityPath}Service.removeById(id)
            ? AjaxResult.success("删除成功")
            : AjaxResult.error("删除失败");
    }

    /**
     * 更新数据
     *
     * @param ${table.entityPath} ${table.comment!}对象
     * @return 更新结果
     */
    @PutMapping
    public AjaxResult update(@RequestBody ${entity} ${table.entityPath}) {
        return ${table.entityPath}Service.updateById(${table.entityPath})
            ? AjaxResult.success("更新成功", ${table.entityPath})
            : AjaxResult.error("更新失败");
    }

    /**
     * 查询单条数据
     *
     * @param id 主键
     * @return 查询结果
     */
    @GetMapping("/{id}")
    public AjaxResult getById(@PathVariable Long id) {
        ${entity} ${table.entityPath} = ${table.entityPath}Service.getById(id);
        return ${table.entityPath} != null 
            ? AjaxResult.success(${table.entityPath})
            : AjaxResult.error("未找到相关数据");
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页大小
     * @return 查询结果
     */
    @GetMapping("/page")
    public AjaxResult page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return AjaxResult.success(${table.entityPath}Service.page(new Page<>(page, size)));
    }

    /**
     * 查询所有数据
     *
     * @return 查询结果
     */
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(${table.entityPath}Service.list());
    }
}
