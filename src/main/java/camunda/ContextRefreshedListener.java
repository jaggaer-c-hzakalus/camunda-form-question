package camunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    protected ProcessEngine processEngine;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent cse) {
        Deployment deploy = processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("bpmn/diagram_1.bpmn")
                .addClasspathResource("bpmn/form_1.form")
                .deploy();

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .createProcessInstanceByKey("NameVerificationProcess")
                .execute();

        Task task = processEngine.getTaskService().createTaskQuery()
                .active()
                .taskDefinitionKey("NameInput")
                .processInstanceId(processInstance.getProcessInstanceId())
                .singleResult();

        // It is okay (we use SPIN library)
        processEngine.getTaskService().setVariable(task.getId(), "bigObject", new BigObject());

        // It is okay too
        processEngine.getTaskService().complete(task.getId(), Map.of(
                "name", "simple text that is okay",
                "bigObject2", new BigObject()
        ));

        // now try to complete the next task from standard tasklist. It will throw
        //     org.h2.jdbc.JdbcBatchUpdateException: Value too long for column "TEXT_ VARCHAR(4000)": "'{""bigBigBigValue"" ...
        // but we even do not use bigBigBigValue in the form!
    }
}