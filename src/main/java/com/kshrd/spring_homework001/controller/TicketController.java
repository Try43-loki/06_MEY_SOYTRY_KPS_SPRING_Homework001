package com.kshrd.spring_homework001.controller;

import com.kshrd.spring_homework001.model.Ticket;
import com.kshrd.spring_homework001.request.RequestTicket;
import com.kshrd.spring_homework001.request.TicketStatus;
import com.kshrd.spring_homework001.request.dto.UpdatePaymentRequest;
import com.kshrd.spring_homework001.respond.CustomResponse;
import com.kshrd.spring_homework001.respond.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/tickets")
public class TicketController {
    // list ticket

    ArrayList<Ticket> ticketList = new ArrayList<>(

            Arrays.asList(

                    new Ticket(1, "Chan", LocalDate.parse("2025-03-10"), "PHN", "BKK", 15.75, true, TicketStatus.BOOKED, "A12"),
                    new Ticket(2, "Sophy", LocalDate.parse("2025-03-15"), "REP", "SGN", 22.50, false, TicketStatus.CANCELLED, "B22"),
                    new Ticket(3, "Vann", LocalDate.parse("2025-03-20"), "KEP", "KPS", 9.99, true, TicketStatus.CANCELLED, "C08"),
                    new Ticket(4, "Malis", LocalDate.parse("2025-03-25"), "BKK", "HCM", 35.99, true, TicketStatus.CANCELLED, "D18"),
                    new Ticket(5, "Kosal", LocalDate.parse("2025-03-30"), "SGN", "PNH", 28.00, false, TicketStatus.COMPLETED, "E05")
            )
    );


    Integer tempId = 5;

    // get all ticket


    @GetMapping
    @Operation(summary = "Get all tickets")
    public ResponseEntity<CustomResponse<PagedResponse<Ticket>>> getTickets(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(new CustomResponse<>(false, "Invalid page or size", HttpStatus.BAD_REQUEST, null));
        }

        int totalElements = ticketList.size();

        if (size >= totalElements) {

            PagedResponse<Ticket> pagedResponse = new PagedResponse<>(ticketList, totalElements, 0, totalElements, 1);

            return ResponseEntity.ok(new CustomResponse<>(true, "All tickets retrieved successfully.", HttpStatus.OK, pagedResponse));
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);


        if (page >= totalPages) {
            return ResponseEntity.ok(new CustomResponse<>(true, "Page exceeds total pages.", HttpStatus.OK, new PagedResponse<>(List.of(), totalElements, page, size, totalPages)));
        }

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<Ticket> paginatedTickets = ticketList.subList(fromIndex, toIndex);

        PagedResponse<Ticket> pagedResponse = new PagedResponse<>(paginatedTickets, totalElements, page, size, totalPages);

        return ResponseEntity.ok(new CustomResponse<>(true, "All tickets retrieved successfully.", HttpStatus.OK, pagedResponse));
    }


    // create new ticket
    @PostMapping()
    @Operation(summary = "Create new ticket")
    public ResponseEntity<CustomResponse<Ticket>> addTicket(@RequestBody RequestTicket ticket) {
        tempId++;

        Ticket newTicket = new Ticket(tempId, ticket.getPassengerName(), ticket.getTravelDate(), ticket.getSourceStation(), ticket.getDestinationStation(), ticket.getPrice(), ticket.getPaymentStatus(), ticket.getTicketStatus(), ticket.getSeatNumber());

        ticketList.add(newTicket);

        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResponse<>(true, "Ticket created successfully", HttpStatus.CREATED, newTicket));
    }



    // get ticket by id
    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by id")
    public ResponseEntity<CustomResponse<Ticket>> getTicket(@PathVariable int id) {

        Optional<Ticket> ticket = ticketList.stream().filter(t -> t.getId().equals(id)).findFirst();

        if (ticket.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse<>(false, "Ticket not found id : " + id , HttpStatus.ACCEPTED, null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse<>(true, "Ticket retrieved successfully", HttpStatus.ACCEPTED, ticket.get()));
    }

    // search by Passenger Name
    @GetMapping("/search")
    @Operation(summary = "Get ticket by Passenger name")
    public ResponseEntity<CustomResponse<Ticket>> getTicket(@RequestParam String name) {

        Optional<Ticket> ticket = ticketList.stream().filter(t -> t.getPassengerName().equalsIgnoreCase(name)).findFirst();

        if (ticket.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse<>(false, "Ticket not found", HttpStatus.NOT_FOUND, null));
        }

        return ResponseEntity.ok(new CustomResponse<>(true, "Ticket retrieved successfully", HttpStatus.OK, ticket.get()));
    }



    @PutMapping("/{id}")
    @Operation(summary = "Update ticket")
    public ResponseEntity<CustomResponse<Ticket>> updateTicket(@PathVariable int id, @RequestBody RequestTicket updatedTicket) {
        Optional<Ticket> ticketOptional = ticketList.stream().filter(t -> t.getId().equals(id)).findFirst();

        if (ticketOptional.isPresent()) {

            Ticket ticket = ticketOptional.get();

            ticket.setPassengerName(updatedTicket.getPassengerName());
            ticket.setTravelDate(updatedTicket.getTravelDate());
            ticket.setSourceStation(updatedTicket.getSourceStation());
            ticket.setDestinationStation(updatedTicket.getDestinationStation());
            ticket.setPrice(updatedTicket.getPrice());
            ticket.setPaymentStatus(updatedTicket.getPaymentStatus());
            ticket.setTicketStatus(updatedTicket.getTicketStatus());
            ticket.setSeatNumber(updatedTicket.getSeatNumber());

            return ResponseEntity.ok(new CustomResponse<>(true, "Ticket updated successfully", HttpStatus.ACCEPTED, ticket));

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse<>(false, "Ticket not found", HttpStatus.OK, null));
        }
    }


    // delete ticket
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ticket")
    public ResponseEntity<CustomResponse<Void>> deleteTicket(@PathVariable int id) {
        Optional<Ticket> isTicket = ticketList.stream().filter(t -> t.getId().equals(id)).findFirst();

        if (isTicket.isPresent()) {

            ticketList.remove(isTicket.get());

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new CustomResponse<>(true, "Ticket deleted successfully", HttpStatus.OK, null));
        } else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse<>(false, "Ticket not found id : " + id, HttpStatus.NOT_FOUND, null));
        }
    }

    // create bulk ticket
    @PostMapping("/bulk")
    @Operation(summary = "Update ticket more than one")
    public ResponseEntity<CustomResponse<List<Ticket>>> addMultipleTickets(@RequestBody List<RequestTicket> tickets) {

        List<Ticket> newTickets = new ArrayList<>();

        for (RequestTicket ticket : tickets) {

            tempId++;

            Ticket newTicket = new Ticket(tempId, ticket.getPassengerName(), ticket.getTravelDate(), ticket.getSourceStation(), ticket.getDestinationStation(), ticket.getPrice(), ticket.getPaymentStatus(), ticket.getTicketStatus(), ticket.getSeatNumber());

            ticketList.add(newTicket);

            newTickets.add(newTicket);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResponse<>(true, "Tickets created successfully", HttpStatus.CREATED, newTickets));
    }

    @PutMapping()
    @Operation(summary = "Update ticket's payment status more than one")
    public ResponseEntity<CustomResponse<List<Ticket>>> updatePaymentStatus(@RequestBody UpdatePaymentRequest request) {

        List<Ticket> updatedTickets = new ArrayList<>();

        request.getTicketIds().forEach(id -> {ticketList.stream().filter(ticket -> ticket.getId().equals(id)).findFirst().ifPresent(ticket -> {ticket.setPaymentStatus(request.getPaymentStatus());updatedTickets.add(ticket);});});

        if (updatedTickets.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse<>(false, "No tickets found for the given IDs", HttpStatus.NOT_FOUND, null));
        }

        return ResponseEntity.ok(new CustomResponse<>(true, "Payment status updated successfully", HttpStatus.OK, updatedTickets));
    }


    @GetMapping("/filter")
    @Operation(summary = "Filter ticket by ticket status and travel date")
    public ResponseEntity<CustomResponse<List<Ticket>>> filterTickets(

            @Parameter(example = "CANCELLED", in = ParameterIn.QUERY)

            @RequestParam(required = false) TicketStatus ticketStatus,

            @Parameter(in = ParameterIn.QUERY)

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate) {

        List<Ticket> filteredTickets = ticketList.stream().filter(t -> (ticketStatus == null || t.getTicketStatus() == ticketStatus)).filter(t -> (travelDate == null || t.getTravelDate().isEqual(travelDate))).collect(Collectors.toList());

        if (filteredTickets.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse<>(false, "No tickets found for the given IDs", HttpStatus.NOT_FOUND, null));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResponse<>(true, "Tickets filter successfully", HttpStatus.CREATED, filteredTickets));
    }

}