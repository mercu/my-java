// 부품 단건에 대해 보유 목록 리스팅하고(유사포함), 증감 메뉴 레이어 노출하기
function myPartWheresModal(partNo, colorId, e) {
    if (typeof e != "undefined") e.preventDefault();

    $('#myModal .modal-title').html("부품-단건 보유 목록 리스팅 및 증감")
    $('#myModal').modal('toggle');

    if (myPartWheresDOM == null) {
        ReactDOM.render(
            <MyPartWheresModalBody
                partNo={partNo}
                colorId={colorId}
            />
            , document.getElementById("myModal-body")
        );
    } else {
        myPartWheresDOM.setState({
            partNo : partNo,
            colorId : colorId
        });
        myPartWheresDOM.loadMyPartWheresSimilar(partType, partNo, colorId);
    }
}

var myPartWheresDOM = null;
class MyPartWheresModalBody extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            partNo : props.partNo,
            colorId : props.colorId,
            myItemWheres : null
        };
    }

    setState(state) {
        super.setState(state);
        console.log(state);
    }

    componentDidMount() {
        myPartWheresDOM = this;
        this.loadMyPartWheresSimilar(this.state.partNo, this.state.colorId);
    }

    componentWillUnmount() {
        myPartWheresDOM = null;
    }

    loadMyPartWheresSimilar(partNo, colorId) {
        $.ajax({
            url:"/admin/myPartWheresSimilar",
            type : "GET",
            dataType : "json",
            data : {
                partNo : partNo,
                colorId : colorId
            },
            contentType: "application/json;charset=UTF-8",
            async : true
        }).done(function(data) {
            this.setState({
                myItemWheres : data
            });
        }.bind(this));
    }

    render() {
        return (
            <div>
                <form id={"partCategoryForm"}>
                    <div className={"form-group"}>
                        myItemWheresSimilar
                    </div>
                </form>
            </div>
        );
    }
}


