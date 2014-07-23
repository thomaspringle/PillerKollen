package se.tpr.pillerkollen.medicines;

import java.util.ArrayList;
import java.util.List;

import se.tpr.pillerkollen.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddRowActivity extends Activity {

	private Context context;
//	private CustomerBean selectedCustomer = null;
//	private ProjectBean selectedProject = null;
//	private TaskBean selectedTask = null;
	private String comment = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_row);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setTitle("Add row");
		
		context = this;
		setButtonListeners();
		disableAddButton();
//		new CustomersTask().execute();
	}

	@Override
	public void finish() {
		super.finish();
	}
	  
	private void setButtonListeners() {
		Button addButton = (Button) findViewById(R.id.add_row_add_button);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addRow();
//				finish();
			}
		});
		Button cancelButton = (Button) findViewById(R.id.add_row_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
	}

	protected void addRow() {
		EditText editText = (EditText) findViewById(R.id.add_row_comment);
		comment = editText.getText().toString();
		comment = comment == null ? "" : comment;
		new AddRowTask().execute();
		
	}

	private void disableAddButton() {
		Button addButton = (Button) findViewById(R.id.add_row_add_button);
		addButton.setEnabled(false);
	}
	private void enableAddButton() {
		Button addButton = (Button) findViewById(R.id.add_row_add_button);
		addButton.setEnabled(true);
	}
	
	public void showErrorDialog(Exception exception) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		Resources res = getResources();
		builder
	      .setTitle(res.getString(R.string.error_title))
	      .setMessage(exception.getMessage())
	      .setPositiveButton(res.getString(R.string.ok_button), null)
	      .show();	
	}
	private void clearProjects() {
		this.selectedProject = null;
		List<ProjectBean> emptyList = new ArrayList<ProjectBean>();
		emptyList.add(ProjectBean.emptyProjectBean());
		populateProjects(emptyList);
		
	}
	private void clearTasks() {
		disableAddButton();
		this.selectedTask = null;
		List<TaskBean> emptyList = new ArrayList<TaskBean>();
		emptyList.add(TaskBean.emptyTaskBean());
		populateTasks(emptyList);
	}
	
	private void populateCustomers(List<CustomerBean> customerBeans) {
		
		Spinner customersDropDown = (Spinner)findViewById(R.id.add_row_customer_spinner);
		ArrayAdapter<CustomerBean> adapter = new ArrayAdapter<CustomerBean>(this, android.R.layout.simple_list_item_1, customerBeans){
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				return getCustomView(position, view);
			}
			
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				
				View view =  super.getDropDownView(position, convertView, parent);
				return getCustomView(position, view);
			}

			private View getCustomView(int position, View view) {
				CustomerBean bean = this.getItem(position);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setText(bean.getName());
				return view;
			}
		};
		 
		customersDropDown.setAdapter(adapter);
		customersDropDown.setOnItemSelectedListener(new OnItemSelectedListener() {
			// Ignore first onItemSelected that is triggered when the spinner is populated
			int count = 0;
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				clearProjects();
				clearTasks();
				count++;
				if (count == 1) {
					return;
				}
				CustomerBean customer = (CustomerBean)parent.getItemAtPosition(pos);
				if (customer.isEmptyCustomer()) {
					return;
				}

				selectedCustomer = customer;
				new ProjectsTask(customer).execute();
				clearTasks();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}

	private void populateProjects(List<ProjectBean> projectBeans) {
		
		Spinner projectsDropDown = (Spinner)findViewById(R.id.add_row_project_spinner);
		ArrayAdapter<ProjectBean> adapter = new ArrayAdapter<ProjectBean>(this, android.R.layout.simple_list_item_1, projectBeans){
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				return getCustomView(position, view);
			}
			
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				
				View view =  super.getDropDownView(position, convertView, parent);
				return getCustomView(position, view);
			}

			private View getCustomView(int position, View view) {
				ProjectBean bean = this.getItem(position);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setText(bean.getName());
				return view;
			}
		};
		 
		projectsDropDown.setAdapter(adapter);
		projectsDropDown.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			int count = 0;
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				clearTasks();
				count++;
				if (count == 1) {
					return;
				}
				ProjectBean project = (ProjectBean)parent.getItemAtPosition(pos);
				if (project.isEmptyProject()) {
					return;
				}

				selectedProject = project;
				new TasksTask(project).execute();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}

	public void populateTasks(List<TaskBean> taskBeans) {
		Spinner tasksDropDown = (Spinner)findViewById(R.id.add_row_task_spinner);
		ArrayAdapter<TaskBean> adapter = new ArrayAdapter<TaskBean>(this, android.R.layout.simple_list_item_1, taskBeans){
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				return getCustomView(position, view);
			}
			
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				
				View view =  super.getDropDownView(position, convertView, parent);
				return getCustomView(position, view);
			}

			private View getCustomView(int position, View view) {
				TaskBean bean = this.getItem(position);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setText(bean.getDescription());
				return view;
			}
		};
		 
		tasksDropDown.setAdapter(adapter);
		tasksDropDown.setOnItemSelectedListener(new OnItemSelectedListener() {
			int count = 0;
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				count++;
				if (count == 1) {
					return;
				}
				TaskBean task = (TaskBean)parent.getItemAtPosition(pos);
				if (task.isEmptyTask()) {
					return;
				}
				selectedTask = task;
				
				enableAddButton();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
	public class CustomersTask extends AsyncTask<Void, Void, List<CustomerBean>> {

		private Exception exception;
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
            progress = new ProgressDialog(context, 0);
            progress.setMessage(getResources().getString(R.string.progress_indicator));
            progress.show();
		}

		@Override
		protected List<CustomerBean> doInBackground(Void... arg0) {
			try {
				List<CustomerBean> customerBeans = AltranHttpClient.customers();
				return customerBeans;

			} catch (Exception e) {
				e.printStackTrace();
				this.exception = e;
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(List<CustomerBean> customerBeans) {
			if (progress != null) {
				progress.dismiss();
			}
			
			if (this.exception != null) {
				showErrorDialog(exception);
			} else {
				if (customerBeans.isEmpty()) {
					customerBeans.add(CustomerBean.nothingFound());
				} else {
					customerBeans.add(0, CustomerBean.selectCustomer());
				}
				populateCustomers(customerBeans);
			}
			super.onPostExecute(customerBeans);
		}
	}

	public class ProjectsTask extends AsyncTask<Void, Void, List<ProjectBean>> {

		private Exception exception;
		private ProgressDialog progress;

		private CustomerBean selectedCustomer;
		
		public ProjectsTask(CustomerBean selectedCustomer) {
			this.selectedCustomer = selectedCustomer;
		}
		
		@Override
		protected void onPreExecute() {
            progress = new ProgressDialog(context, 0);
            progress.setMessage(getResources().getString(R.string.progress_indicator));
            progress.show();
		}

		@Override
		protected List<ProjectBean> doInBackground(Void... arg0) {
			try {
				List<ProjectBean> projectBeans = AltranHttpClient.projects(selectedCustomer);
				return projectBeans;

			} catch (Exception e) {
				e.printStackTrace();
				this.exception = e;
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(List<ProjectBean> projectBeans) {
			if (progress != null) {
				progress.dismiss();
			}
			
			if (this.exception != null) {
				showErrorDialog(exception);
			} else {
				if (projectBeans.isEmpty()) {
					projectBeans.add(ProjectBean.nothingFound());
				} else {
					projectBeans.add(0, ProjectBean.selectProject());
				}
				populateProjects(projectBeans);
			}
			super.onPostExecute(projectBeans);
		}
	}
	
	public class TasksTask extends AsyncTask<Void, Void, List<TaskBean>> {

		private Exception exception;
		private ProgressDialog progress;

		private ProjectBean selectedProject;
		
		public TasksTask(ProjectBean selectedProject) {
			this.selectedProject = selectedProject;
		}
		
		@Override
		protected void onPreExecute() {
            progress = new ProgressDialog(context, 0);
            progress.setMessage(getResources().getString(R.string.progress_indicator));
            progress.show();
		}

		@Override
		protected List<TaskBean> doInBackground(Void... arg0) {
			try {
				List<TaskBean> taskBeans = AltranHttpClient.tasks(selectedProject);
				return taskBeans;

			} catch (Exception e) {
				e.printStackTrace();
				this.exception = e;
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(List<TaskBean> taskBeans) {
			if (progress != null) {
				progress.dismiss();
			}
			
			if (this.exception != null) {
				showErrorDialog(exception);
			} else {
				if (taskBeans.isEmpty()) {
					taskBeans.add(TaskBean.nothingFound());
				} else {
					taskBeans.add(0, TaskBean.selectTask());
				}
				populateTasks(taskBeans);
			}
			super.onPostExecute(taskBeans);
		}
	}

	public class AddRowTask extends AsyncTask<Void, Void, String> {

		private Exception exception;
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
            progress = new ProgressDialog(context, 0);
            progress.setMessage(getResources().getString(R.string.progress_indicator));
            progress.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				String lineNo = AltranHttpClient.addRow(selectedCustomer, selectedProject, selectedTask, comment);
				return lineNo;

			} catch (Exception e) {
				e.printStackTrace();
				this.exception = e;
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(String lineNo) {
			if (progress != null) {
				progress.dismiss();
			}
			
			if (this.exception != null) {
				showErrorDialog(exception);
			} else {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
			super.onPostExecute(lineNo);
		}
	}
}
