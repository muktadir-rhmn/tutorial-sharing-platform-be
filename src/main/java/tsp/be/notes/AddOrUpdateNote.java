package tsp.be.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.notes.models.NotesRepository;
import tsp.be.user.UserDescriptor;
import tsp.be.utils.SingleMessageResponse;

import static tsp.be.notes.MetaData.ANNOTATION_ROOT_PATH;

class AddOrUpdateNoteRequest {
	public String note;
}

@RestController
@RequestMapping(ANNOTATION_ROOT_PATH)
public class AddOrUpdateNote {
	@Autowired
	private NotesRepository notesRepository;

	//todo: change it to PUT
	@PostMapping("/{lessonID}")
	public SingleMessageResponse updateNote(@RequestAttribute("user")UserDescriptor userDescriptor, @PathVariable String lessonID, @RequestBody AddOrUpdateNoteRequest request) {
		notesRepository.addOrUpdateNote(userDescriptor.getUserID(), lessonID, request.note);

		return new SingleMessageResponse("Operation success");
	}

}
