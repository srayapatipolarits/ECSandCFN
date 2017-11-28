package com.sp.web.repository.note;

import com.sp.web.model.note.UserNote;
import com.sp.web.repository.generic.GenericMongoRepository;

/**
 * NoteRepository handles the notes functionality for the notes.
 * 
 * @author pradeepruhil
 *
 */
public interface NoteRepository extends GenericMongoRepository<UserNote> {
  
}
