package com.sp.web.repository.note;

import com.sp.web.model.note.UserNote;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Component;

/**
 * MongoNoteRepository class is the note repository class user note.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class MongoNoteRepository extends GenericMongoRepositoryImpl<UserNote> implements
    NoteRepository {
  
}
