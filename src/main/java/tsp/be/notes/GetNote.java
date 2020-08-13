package tsp.be.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.notes.models.NotesRepository;
import tsp.be.user.UserDescriptor;

import static tsp.be.notes.MetaData.ANNOTATION_ROOT_PATH;

class GetNoteResponse {
	public String note;
}

@RestController
@RequestMapping(ANNOTATION_ROOT_PATH)
public class GetNote {
	@Autowired
	private NotesRepository notesRepository;

	@GetMapping("/{lessonID}")
	public GetNoteResponse getNote(@RequestAttribute("user") UserDescriptor userDescriptor,  @PathVariable String lessonID) {
		GetNoteResponse response = new GetNoteResponse();
		response.note = notesRepository.getNote(userDescriptor.getUserID(), lessonID);
		return response;
	}
}
