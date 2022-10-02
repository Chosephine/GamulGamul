import { FC, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom'
import { removeGoods,updateFavoriteItems } from '@modules/FavoriteProductList';
import type { RootState, AppDispatch} from '@modules/store';
interface SelectedAreaProps {
  
}
 
const SelectedArea: FC<SelectedAreaProps> = () => {
  const navigate = useNavigate();
  const {goods} = useSelector((state : RootState) => {
    
    console.log(state);
    return ({
      goods : state.persistedReducer.favoriteProductListReducer.goods
    })
  });
  const dispatch = useDispatch<AppDispatch>();
  const removeList = (goodsItem : { goodsId : number })=>{
    dispatch(removeGoods(goodsItem))
  }

  const updateItem = async ()=>{
    const goodsList = goods.map((item : { goodsId : number})=>{
      return item.goodsId
    })
    console.log(goodsList);
    const data = await dispatch(updateFavoriteItems(goodsList)).unwrap();
    console.log(data);
    if(data){
      navigate('/favorite')
    }
  }


  return (<>
  <div className="flex h-full w-full flex-col bg-[#b3d1e6]">
    <div className="my-3 mx-5 text-lg">
      즐겨찾기 목록
      <span className="ml-2 text-xs text-gray-500">
        상품을 선택하면 목록에서 제거할 수 있어요.
      </span>
    </div>
    <div className="flex h-full flex-wrap content-start items-start overflow-auto scroll-auto p-3">
      {goods && goods.map((x : { goodsId : number, goodsName : string }, idx : number) =>{
        return (
          <span className="m-1 rounded-full bg-white px-2 py-1 text-sm" key={idx} onClick={()=>{removeList(x)}}>
            {x.goodsName}
          </span>
          )
        })}
    </div>
    <span onClick={()=>{updateItem()}} className="relate bottom-0 right-0 m-3 w-auto self-end rounded-full border border-gray-600 bg-white px-3 py-1">
      다음 &gt;
    </span>
  </div>
  </>);
}
 
export default SelectedArea;