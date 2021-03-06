package dao.impl;

import dao.BaseDao;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


public class BaseDaoImpl<T> extends HibernateDaoSupport implements BaseDao<T> {

    @Resource(name = "sessionFactory")
    public void setSf(SessionFactory sf) {
        super.setSessionFactory(sf);
    }

    //用于接收运行期泛型类型
    private Class clazz;

    public BaseDaoImpl() {
        ParameterizedType ptClass = (ParameterizedType) this.getClass().getGenericSuperclass();
        //获得运行期的泛型类型
        clazz = (Class) ptClass.getActualTypeArguments()[0];
    }

    @Override
    public void savetOrUpdate(T t) {
        getHibernateTemplate().saveOrUpdate(t);

    }

    @Override
    public void save(T t) {
        getHibernateTemplate().save(t);
    }

    @Override
    public void delete(T t) {
        getHibernateTemplate().delete(t);
    }

    @Override
    public void delete(Serializable id) {
        T t = this.getById(id);//先取再删
        getHibernateTemplate().delete(t);

    }

    @Override
    public void update(T t) {
        getHibernateTemplate().update(t);
    }

    @Override
    public T getById(Serializable id) {
       return (T)getHibernateTemplate().get(clazz,id);
    }

    @Override
    public Integer getTotalCount(DetachedCriteria dc) {
        dc.setProjection(Projections.rowCount());
        List<Long> list = (List<Long>) getHibernateTemplate().findByCriteria(dc);
        // 清空之前设置的聚合函数
        dc.setProjection(null);
        if (list != null && list.size() > 0) {
            Long count = list.get(0);
            return count.intValue();
        } else {
            return null;
        }
    }

    @Override
    public List<T> getPageList(DetachedCriteria dc, Integer start, Integer pageSize) {
        List<T> list = (List<T>) getHibernateTemplate().findByCriteria(dc, start, pageSize);
        return list;
    }
}
