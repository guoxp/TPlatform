package org.tplatform.auth.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.tplatform.auth.entity.SysUser;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

/**
 * Created by Tianyi on 2016/5/28.
 */
public interface SysUserMapper extends Mapper<SysUser> {

  @Override
  @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "record.id", before = false, resultType = Long.class)
  @Insert("INSERT INTO sys_auth_user ( id,createTime,operator,status,username,nickname,password,mobile,email,avatarUrl ) " +
      "VALUES( #{record.id},#{record.createTime},#{record.operator},#{record.status},#{record.username}," +
      "#{record.nickname},#{record.password},#{record.mobile},#{record.email},#{record.avatarUrl} )")
  int insert(@Param("record") SysUser record);

  /**
   * 修改密码
   * @param id
   * @param password
   * @return
   */
  @Update("UPDATE SET password = ${password} WHERE id = #{id}")
  int updatePassword(@Param("id") Long id, @Param("password") String password);


  @InsertProvider(type = SQLProvider.class, method = "saveWithRole")
  int saveWithRole(@Param("sysUser") SysUser sysUser, @Param("roles") Long[] roles);

  class SQLProvider {

    public String saveWithRole(Map param) {
      Long[] roles = (Long[]) param.get("roles");
      StringBuilder sql = new StringBuilder();
      sql.append("DELETE FROM sys_auth_user_role WHERE user_id = #{sysUser.id};\n");
      sql.append("INSERT INTO sys_auth_user_role (user_id, role_id) VALUES");
      for (int i = 0, size = roles.length; i < size; i++) {
        sql.append("(#{sysUser.id},")
            .append(roles[i])
            .append("),");
      }
      return sql.substring(0, sql.length() - 1);
    }
  }
}
