package com.server;
import org.eclipse.microprofile.faulttolerance.Timeout;
import com.server.model.Program;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.temporal.ChronoUnit;

@Path("/api/run-program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class RunProgram {

    @POST
    @Timeout(value =3, unit = ChronoUnit.SECONDS)
    public Result Program(Program program){
        return StringLoxRunner.runProgram(program.program);
    }
}
