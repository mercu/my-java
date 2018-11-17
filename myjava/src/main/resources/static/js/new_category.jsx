var newPartCategoryDOM = null;
function newPartCategoryModal(parentId, e) {
    if (typeof e != "undefined") e.preventDefault();

    $('#myModal .modal-title').html("카테고리 생성하기")
    $('#myModal').modal('toggle');

    if (newPartCategoryDOM == null) {
        ReactDOM.render(
            <NewCategoryModalBody parentId={parentId}/>
            , document.getElementById("myModal-body")
        );
    } else {
        newPartCategoryDOM.setState({
            parentId : parentId
        });
    }
}

class NewCategoryModalBody extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            parentId : props.parentId
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        newPartCategoryDOM = this;
    }

    componentWillUnmount() {
        newPartCategoryDOM = null;
    }

    render() {
        return (
            <div>
                <form id={"partCategoryForm"}>
                    <input type={"hidden"} name={"parentId"} value={this.state.parentId}/>
                    <div className={"form-group"}>
                        <label htmlFor={"name"}>카테고리명</label>
                        <input type={"text"} className={"form-control"} name={"name"}/>
                    </div>
                </form>
                <button type={"submit"} className={"btn btn-primary"} onClick={(e) => newPartCategory($("#partCategoryForm"), e)}>생성하기</button>
            </div>
        );
    }
}

function newPartCategory(form, e) {
    if (typeof e != "undefined") e.preventDefault();

    $.ajax({
        url:"/admin/partCategory/new",
        type : "POST",
        dataType : "json",
        data : {
            "parentId" : $("#partCategoryForm [name=parentId]").val(),
            "name" : $("#partCategoryForm [name=name]").val()
        },
        ContentType: "application/json",
        async : true
    }).always(function(data) {
        alert(data.responseText);
        $("#myModal").modal("toggle");
        partCategories($("#partCategoryForm [name=parentId]").val());
    });

}

