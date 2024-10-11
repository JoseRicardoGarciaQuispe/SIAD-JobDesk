package gob.presidencia.siad.model;

import java.util.List;

import lombok.Data;

@Data
public class DataTable<T> {
	
	private int draw;
    private int start;
    private long recordsTotal;
    private long recordsFiltered;
    private List<T> data;
}
