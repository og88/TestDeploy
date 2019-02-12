package com.revature.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.revature.backend.MasterServlet;
import com.revature.module.Employee;
import com.revature.util.JDBCConnectionUtil;

public class EmployeeDaoImplementation implements EmployeeDao {
	private static EmployeeDaoImplementation employeeImp;
	
	final static Logger log = Logger.getLogger(EmployeeDaoImplementation.class);


	private EmployeeDaoImplementation() {

	}

	public static EmployeeDaoImplementation getEmployeeDao() {
		if (employeeImp == null) {
			employeeImp = new EmployeeDaoImplementation();
		}
		return employeeImp;
	}

	public List<Employee> viewEmployee() throws SQLException {
		try (Connection conn = JDBCConnectionUtil.getConnection()) {
			String sql = "SELECT * FROM EMPLOYEE";
			Statement stmnt = conn.createStatement();
			ResultSet results = stmnt.executeQuery(sql);
			List<Employee> allEmp = new ArrayList<Employee>();
			while (results.next()) {
				allEmp.add(new Employee(results.getInt(1), results.getString(2), results.getString(3),
						results.getString(4), "**HIDDEN**", results.getInt(6), results.getString(7)));
			}
			return allEmp;
		}
	}

	public Employee login(String username, String password) throws SQLException {
		log.info(username + " " + password);
		try (Connection conn = JDBCConnectionUtil.getConnection()) {
			String sql = "SELECT * FROM EMPLOYEE WHERE E_PASSWORD LIKE GET_USER_HASH(?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				System.out.println("found");
				return new Employee(result.getInt("E_ID"), result.getString("E_FIRST"), result.getString("E_LAST"),
						result.getString("E_USERNAME"), result.getString("E_PASSWORD"), result.getInt("E_MANAGER"),
						result.getString("EMAIL"));
			}
		}
		log.warn("not found\n");
		return null;
	}

	@Override
	public Employee register(Employee newEmp) throws SQLException {
		try (Connection conn = JDBCConnectionUtil.getConnection()) {
			String sql = "CALL INSERT_EMPLOYEE(?,?,?,?,?,?)";
			CallableStatement cs = conn.prepareCall(sql);
			cs.setString(1, newEmp.getFirstName());
			cs.setString(2, newEmp.getLastName());
			cs.setString(3, newEmp.getUsername());
			cs.setString(4, newEmp.getPassword());
			cs.setString(6, newEmp.getEmail());
			if (newEmp.isManager()) {
				cs.setInt(5, 1);
			} else {
				cs.setInt(5, 0);
			}
			cs.execute();
		}
		return login(newEmp.getUsername(), newEmp.getPassword());
	}

	@Override
	public Employee update(Employee emp) throws SQLException {
		try (Connection conn = JDBCConnectionUtil.getConnection()) {
			String sql = "CALL UPDATE_EMP(?,?,?,?,?,?,?)";
			CallableStatement cs = conn.prepareCall(sql);
			cs.setInt(1, emp.getId());
			cs.setString(2, emp.getFirstName());
			cs.setString(3, emp.getLastName());
			cs.setString(4, emp.getUsername());
			cs.setString(5, emp.getPassword());
			cs.setString(7, emp.getEmail());
			if (emp.isManager()) {
				cs.setInt(6, 1);
			} else {
				cs.setInt(6, 0);
			}
			cs.execute();
		}
		return emp;
	}

	@Override
	public Employee getEmployee(int id) throws SQLException {
		log.info("Getting all users");
		try (Connection conn = JDBCConnectionUtil.getConnection()) {
			String sql = "SELECT * FROM EMPLOYEE WHERE E_ID = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				log.info("found");
				return new Employee(result.getInt("E_ID"), result.getString("E_FIRST"), result.getString("E_LAST"),
						result.getString("E_USERNAME"), result.getString("E_PASSWORD"), result.getInt("E_MANAGER"),
						result.getString("EMAIL"));
			}
		}
		return null;
	}
}
