class PartList extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }

    render() {
        var element = null;
        var categoryId = this.props.categoryId;
        var parentId = this.props.parentId;
        $.ajax({
            url:"/partList?categoryId=" + categoryId,
            type : "GET",
            dataType : "json",
            ContentType: "application/json",
            async : false
        }).done(function(data){
            element =
                <div className={'panel panel-default'}>
                    <div className={'panel-heading'} style={{position:'fixed'}}>
                        <p>
                            <button className={'btn btn-primary'} onClick={(e) => partCategories(parentId, e)}>상위</button>
                        </p>
                    </div>
                    <div className={'panel-body'}>
                        <table className="table table-bordered">
                            <thead>
                            <tr>
                                <th>img</th>
                                <th>partNo</th>
                                <th>setQty</th>
                                <th>partName</th>
                            </tr>
                            </thead>
                            <tbody>
                            {data.map(function(item, key) {
                                return <tr key={key}>
                                    <td><img src={item.img}/></td>
                                    <td><a href={'https://www.bricklink.com/v2/catalog/catalogitem.page?id=' + item.id + '#T=C'} target={'_blank'}>{item.partNo}</a></td>
                                    <td>{item.setQty}</td>
                                    <td>{item.partName}</td>
                                </tr>
                            })}
                            </tbody>
                        </table>
                    </div>
                </div>
        });
        return element;
    }

}

function partList(categoryId, parentId, e) {
    if (typeof e != "undefined") e.preventDefault();

    ReactDOM.render(
        <PartList categoryId={categoryId} parentId={parentId}/>
        , document.getElementById("main")
    );
    $("#layer_1").show();
}
