public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        System.out.println("Kanban App Test:\n");
        String testStage1Log = "1. CREATE @ ";

        Task hobbyTask = new Task("Violin", "Try to play Vivaldi \"The Four Seasons\"");
        Integer hobbyTaskID = taskManager.createTask(hobbyTask);
        testStage1Log += "Task id=" + hobbyTaskID + ", ";

        Task houseTask = new Task("Cleaning", "Vacuum and wash the floors at home");
        Integer houseTaskID = taskManager.createTask(houseTask);
        testStage1Log += "Task id=" + houseTaskID + ", ";

        Epic workEpic = new Epic("Kanban App", "Working on Kanban App for this week");
        Integer workEpicID = taskManager.createEpic(workEpic);
        testStage1Log += "Epic id=" + workEpicID + ", ";

        Subtask workSubtask1 = new Subtask("Presentation", "Prepare a basic presentation layout");
        workSubtask1.setEpicID(workEpicID);
        Integer workSubtask1ID = taskManager.createSubtask(workSubtask1);
        testStage1Log += "Subtask id=" + workSubtask1ID + ", ";

        Subtask workSubtask2 = new Subtask("Test report", "Prepare a backend test report");
        workSubtask2.setEpicID(workEpicID);
        Integer workSubtask2ID = taskManager.createSubtask(workSubtask2);
        testStage1Log += "Subtask id=" + workSubtask2ID + ", ";

        Epic educationEpic = new Epic("Education", "List of education tasks for the week");
        Integer educationEpicID = taskManager.createEpic(educationEpic);
        testStage1Log += "Epic id=" + educationEpicID + ", ";

        Subtask educationSubtask1 = new Subtask("Chinese", "Translate a few short poems");
        educationSubtask1.setEpicID(educationEpicID);
        Integer educationSubtask1ID = taskManager.createSubtask(educationSubtask1);
        testStage1Log += "Subtask id=" + educationSubtask1ID;

        printTaskManagerTestReport(taskManager, testStage1Log);

        // 2. HISTORY
        taskManager.getTaskByID(hobbyTaskID);
        taskManager.getTaskByID(houseTaskID);

        taskManager.getEpicByID(workEpicID);
        taskManager.getSubtaskByID(workSubtask1ID);
        taskManager.getSubtaskByID(workSubtask2ID);

        taskManager.getEpicByID(educationEpicID);
        taskManager.getSubtaskByID(educationSubtask1ID);

        System.out.println("2. HISTORY @ View all tasks created in 1 \n");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();

        String testStage2Log = "3. UPDATE @ ";

        hobbyTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(hobbyTask);
        testStage2Log += "Task id=" + hobbyTaskID + "(" + hobbyTask.getStatus() +"), ";

        houseTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(houseTask);
        testStage2Log += "Task id=" + houseTaskID + "(" + houseTask.getStatus() +"), ";

        workSubtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(workSubtask1);
        testStage2Log += "Subtask id=" + workSubtask1ID +  "(" + workSubtask1.getStatus() +"), ";

        educationSubtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(educationSubtask1);
        testStage2Log += "Subtask id=" + educationSubtask1ID +  "(" + educationSubtask1.getStatus() +"), ";

        printTaskManagerTestReport(taskManager, testStage2Log);

        String testStage3Log = "4. REMOVE @ ";

        taskManager.removeTaskByID(houseTaskID);
        testStage3Log += "Task id=" + houseTaskID + ", ";

        taskManager.removeEpicByID(educationEpicID);
        testStage3Log += "Epic id=" + educationEpicID + ", ";

        taskManager.removeSubtaskByID(workSubtask1ID);
        testStage3Log += "Subtask id=" + workSubtask1ID + ", ";

        printTaskManagerTestReport(taskManager, testStage3Log);
    }

    static void printTaskManagerTestReport(TaskManager taskManager, String testStageLog) {
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