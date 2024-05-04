import org.jetbrains.annotations.NotNull;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Kanban App Test:\n");
        String testStage1Log = "1. CREATE @ ";

        Task hobbyTask = new Task("Violin", "Try to play Vivaldi \"The Four Seasons\"");
        Integer hobbyTaskID = taskManager.create(hobbyTask);
        testStage1Log += "Task id=" + hobbyTaskID + ", ";

        Task houseTask = new Task("Cleaning", "Vacuum and wash the floors at home");
        Integer houseTaskID = taskManager.create(houseTask);
        testStage1Log += "Task id=" + houseTaskID + ", ";

        Epic workEpic = new Epic("Kanban App", "Working on Kanban App for this week");
        Integer workEpicID = taskManager.create(workEpic);
        testStage1Log += "Epic id=" + workEpicID + ", ";

        Subtask workSubtask1 = new Subtask("Presentation", "Prepare a basic presentation layout");
        Integer workSubtask1ID = taskManager.create(workSubtask1, workEpicID);
        testStage1Log += "Subtask id=" + workSubtask1ID + ", ";

        Subtask workSubtask2 = new Subtask("Test report", "Prepare a backend test report");
        Integer workSubtask2ID = taskManager.create(workSubtask2, workEpicID);
        testStage1Log += "Subtask id=" + workSubtask2ID + ", ";

        Epic educationEpic = new Epic("Education", "List of education tasks for the week");
        Integer educationEpicID = taskManager.create(educationEpic);
        testStage1Log += "Epic id=" + educationEpicID + ", ";

        Subtask educationSubtask1 = new Subtask("Chinese", "Translate a few short poems");
        Integer educationSubtask1ID = taskManager.create(educationSubtask1, educationEpicID);
        testStage1Log += "Subtask id=" + educationSubtask1ID;

        printTaskManagerTestReport(taskManager, testStage1Log);

        String testStage2Log = "2. UPDATE @ ";

        hobbyTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.update(hobbyTask);
        testStage2Log += "Task id=" + hobbyTaskID + "(" + hobbyTask.getStatus() +"), ";

        houseTask.setStatus(TaskStatus.DONE);
        taskManager.update(houseTask);
        testStage2Log += "Task id=" + houseTaskID + "(" + houseTask.getStatus() +"), ";

        workSubtask1.setStatus(TaskStatus.DONE);
        taskManager.update(workSubtask1);
        testStage2Log += "Subtask id=" + workSubtask1ID +  "(" + workSubtask1.getStatus() +"), ";

        educationSubtask1.setStatus(TaskStatus.DONE);
        taskManager.update(educationSubtask1);
        testStage2Log += "Subtask id=" + educationSubtask1ID +  "(" + educationSubtask1.getStatus() +"), ";

        printTaskManagerTestReport(taskManager, testStage2Log);

        String testStage3Log = "3. REMOVE @ ";

        taskManager.removeTaskByID(houseTaskID);
        testStage3Log += "Task id=" + houseTaskID + ", ";

        taskManager.removeEpicByID(educationEpicID);
        testStage3Log += "Epic id=" + educationEpicID + ", ";

        taskManager.removeSubtaskByID(workSubtask1ID);
        testStage3Log += "Subtask id=" + workSubtask1ID + ", ";

        printTaskManagerTestReport(taskManager, testStage3Log);
    }

    static void printTaskManagerTestReport(@NotNull TaskManager taskManager, String testStageLog) {
        System.out.println(testStageLog + "\n");

        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println();

        for (Epic epic : taskManager.getAllEpic()) {
            System.out.println(epic);
        }

        System.out.println();

        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println();
    }
}