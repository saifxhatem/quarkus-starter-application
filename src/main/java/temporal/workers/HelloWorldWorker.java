package temporal.workers;

import org.jboss.logging.Logger;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import temporal.activities.impl.CapitalizerImpl;
import temporal.queues.Queues;
import temporal.workflows.impl.HelloWorldWorkflowImpl;

public class HelloWorldWorker implements TemporalWorker {

    private static final Logger LOG = Logger.getLogger(HelloWorldWorker.class);

    public void startWorker() {
        // This gRPC stubs wrapper talks to the local docker instance of the Temporal service.
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        // Create a Worker factory that can be used to create Workers that poll specific Task Queues.
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(Queues.HELLO_WORLD_TASK_QUEUE);
        // This Worker hosts both Workflow and Activity implementations.
        // Workflows are stateful, so you need to supply a type to create instances.
        worker.registerWorkflowImplementationTypes(HelloWorldWorkflowImpl.class);
        worker.registerActivitiesImplementations(
            new CapitalizerImpl()
        );
        // Start polling the Task Queue.
        factory.start();
        LOG.info("Started HelloWorld worker");
    }
}