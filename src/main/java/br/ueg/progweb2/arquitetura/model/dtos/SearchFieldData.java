package br.ueg.progweb2.arquitetura.model.dtos;

import br.ueg.progweb2.arquitetura.interfaces.ISearchFieldData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFieldData implements ISearchFieldData<String> {
    private String id;
    private String searchDescription;
    public SearchFieldData(ISearchFieldData<?> data){
        this.id = String.valueOf(data.getId());
        this.searchDescription = data.getSearchDescription();
    }
}
