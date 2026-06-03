package com.server;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.faulttolerance.Timeout;
import com.server.model.Program;
import jakarta.ws.rs.core.MediaType;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

@Path("/api/run-program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class RunProgram {

    private static final ExecutorService executor =
            Executors.newCachedThreadPool();

    @POST
    @Timeout(value = 3, unit = ChronoUnit.SECONDS)
    public Result Program(Program program){
        Future<Result> future = executor.submit(
                () -> StringLoxRunner.runProgram(program.program)
        );
        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // interrupt execution
            throw new BadRequestException("Execution timed out after 3 seconds");
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
