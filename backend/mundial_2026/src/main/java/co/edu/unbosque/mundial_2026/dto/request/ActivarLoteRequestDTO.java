package co.edu.unbosque.mundial_2026.dto.request;

import java.util.List;

public class ActivarLoteRequestDTO {

    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}