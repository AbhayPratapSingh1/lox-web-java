package com.server;


import com.server.model.Program;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/run-program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RunProgram {

    @POST
    public Result Program(Program program){
        System.out.println(program);
        System.out.println(StringLoxRunner.runProgram(program.program));
        System.out.println(StringLoxRunner.runProgram(program.program));
        System.out.println(StringLoxRunner.runProgram(program.program));
        return StringLoxRunner.runProgram(program.program);
    }
}
