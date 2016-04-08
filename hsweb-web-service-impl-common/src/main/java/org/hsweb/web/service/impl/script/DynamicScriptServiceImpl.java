package org.hsweb.web.service.impl.script;

import org.hsweb.web.bean.po.script.DynamicScript;
import org.hsweb.web.dao.script.DynamicScriptMapper;
import org.hsweb.web.exception.BusinessException;
import org.hsweb.web.service.impl.AbstractServiceImpl;
import org.hsweb.web.service.script.DynamicScriptService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * 动态脚本服务类
 * Created by generator
 */
@Service("dynamicScriptService")
public class DynamicScriptServiceImpl extends AbstractServiceImpl<DynamicScript, String> implements DynamicScriptService {

    private static final String CACHE_KEY = "dynamic_script";

    //默认数据映射接口
    @Resource
    protected DynamicScriptMapper dynamicScriptMapper;

    @Override
    protected DynamicScriptMapper getMapper() {
        return this.dynamicScriptMapper;
    }

    @Override
    @Cacheable(value = CACHE_KEY, key = "#pk")
    public DynamicScript selectByPk(String pk) throws Exception {
        return super.selectByPk(pk);
    }

    @Override
    @CacheEvict(value = CACHE_KEY, key = "#data.u_id")
    public int update(DynamicScript data) throws Exception {
        int i = super.update(data);
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(data.getType());
        engine.compile(data.getU_id(), data.getContent());
        return i;
    }

    @Override
    @CacheEvict(value = CACHE_KEY, allEntries = true)
    public int update(List<DynamicScript> datas) throws Exception {
        int i = super.update(datas);
        for (DynamicScript data : datas) {
            DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(data.getType());
            engine.compile(data.getU_id(), data.getContent());
        }
        return i;
    }

    @Override
    @CacheEvict(value = CACHE_KEY, key = "#pk")
    public int delete(String pk) throws Exception {
        return super.delete(pk);
    }

    public void compile(String id) throws Exception {
        DynamicScript script = this.selectByPk(id);
        if (script == null) throw new BusinessException(String.format("脚本[%s]不存在", id));
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(script.getType());
        try {
            engine.compile(script.getU_id(), script.getContent());
        } catch (Exception e) {
            logger.error("compile error!", e);
        }
    }

    public void compileAll() throws Exception {
        List<DynamicScript> list = this.select();
        for (DynamicScript script : list) {
            DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(script.getType());
            try {
                engine.compile(script.getU_id(), script.getContent());
            } catch (Exception e) {
                logger.error("compile error!", e);
            }
        }
    }

}